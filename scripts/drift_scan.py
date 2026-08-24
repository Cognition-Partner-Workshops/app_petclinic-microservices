#!/usr/bin/env python3
"""Dependency drift scanner for the petclinic-microservices Maven build.

Collects, across the aggregator and every module:
  - declared vs. latest release versions (Maven Central maven-metadata.xml)
  - known advisories (OSV.dev)

Emits one normalized JSON document on stdout (or to --out) that matches
timesheet-app/docs/automations/remediation-queue.schema.json, so the weekly drift
report and its remediation queue are reproducible instead of hand-assembled.
See docs/automations/weekly-drift-report.md.

Read-only: never edits a pom, never runs a plugin that rewrites versions.
Dependency coordinates come from `mvn dependency:list`; if Maven is unavailable
or offline, the scanner falls back to parsing the poms directly and records the
degradation under "errors".
"""

import argparse
import json
import os
import re
import subprocess
import sys
import tempfile
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from datetime import datetime, timezone

REPO = "Cognition-Partner-Workshops/petclinic-microservices"
POM_NS = {"m": "http://maven.apache.org/POM/4.0.0"}
CENTRAL_REPO = "https://repo1.maven.org/maven2"
OSV_QUERYBATCH = "https://api.osv.dev/v1/querybatch"
OSV_VULN = "https://api.osv.dev/v1/vulns/"

SEVERITY_POINTS = {"CRITICAL": 50, "HIGH": 30, "MODERATE": 12, "MEDIUM": 12, "LOW": 4, "INFO": 1}
EXPOSURE_POINTS = {"runtime": 10, "test": 3, "plugin": 3, "build_parent": 12, "runtime_platform": 12}
# Components whose major bumps ripple through every module.
HIGH_BLAST_RADIUS = {
    "org.springframework.boot:spring-boot-starter-parent",
    "org.springframework.cloud:spring-cloud-dependencies",
    "org.springframework.ai:spring-ai-bom",
    "java",
}


def resolve_within(base, *parts):
    """Path under base, or None when the joined path escapes it (--root is user input)."""
    base = os.path.realpath(base)
    candidate = os.path.realpath(os.path.join(base, *parts))
    return candidate if candidate == base or candidate.startswith(base + os.sep) else None


def output_bases():
    """Directories --out may write into: the working tree and the temp directories."""
    candidates = [os.getcwd(), tempfile.gettempdir(), "/tmp"]
    return tuple(sorted({os.path.realpath(c) for c in candidates if os.path.isdir(c)}))


def resolve_output(path):
    """--out is user input, so it is validated against output_bases() before opening."""
    candidate = os.path.realpath(path)
    bases = output_bases()
    if any(candidate.startswith(base + os.sep) for base in bases):
        return candidate
    raise SystemExit(f"--out must be inside one of {', '.join(bases)}: {path}")


def http_json(url, payload=None, timeout=30):
    data = json.dumps(payload).encode() if payload is not None else None
    request = urllib.request.Request(
        url, data=data, headers={"Content-Type": "application/json", "Accept": "application/json"}
    )
    with urllib.request.urlopen(request, timeout=timeout) as response:
        return json.loads(response.read().decode())


def parse_version(value):
    parts = [int(p) for p in re.findall(r"\d+", value or "")[:3]]
    return parts + [0] * (3 - len(parts))


def version_gap(current, latest):
    cur, new = parse_version(current), parse_version(latest)
    return {
        "major": max(0, new[0] - cur[0]),
        "minor": max(0, new[1] - cur[1]) if new[0] == cur[0] else 0,
        "patch": max(0, new[2] - cur[2]) if new[:2] == cur[:2] else 0,
    }


def is_prerelease(version):
    return bool(re.search(r"(?i)(alpha|beta|rc|m\d+|snapshot|preview|cr\d+)", version or ""))


def rank_score(item):
    severities = [SEVERITY_POINTS.get(a["severity"].upper(), 4) for a in item["advisories"]]
    severity = max(severities) if severities else 0
    volume = min(15, 5 * max(0, len(item["advisories"]) - 1))
    gap = item["gap"]
    staleness = min(24, 8 * gap["major"]) + min(6, gap["minor"]) + (2 if gap["patch"] else 0)
    return severity + volume + staleness + EXPOSURE_POINTS.get(item["exposure"], 5)


def size_estimate(item):
    gap = item["gap"]
    if gap["major"] >= 1:
        return "L" if item["component"] in HIGH_BLAST_RADIUS else "M"
    if gap["minor"] >= 1:
        return "S"
    return "S" if item["advisories"] else "XS"


def dependencies_from_maven(root, errors):
    """group:artifact -> (version, scope) via `mvn dependency:list`."""
    command = ["mvn", "-B", "-q", "-DincludeParents=true", "-DoutputAbsoluteArtifactFilename=false",
               "dependency:list"]
    mvnw = os.path.join(root, "mvnw")
    if os.path.isfile(mvnw):
        command[0] = mvnw
    try:
        proc = subprocess.run(command, cwd=root, capture_output=True, text=True, timeout=1800)
    except (OSError, subprocess.SubprocessError) as exc:
        errors.append(f"mvn dependency:list failed ({exc}); fell back to pom parsing")
        return {}
    if proc.returncode != 0:
        errors.append(f"mvn dependency:list exited {proc.returncode}; fell back to pom parsing")
        return {}
    found = {}
    pattern = re.compile(r"^\s*(?:\[INFO\]\s+)?([\w.\-]+):([\w.\-]+):(?:\w+):([\w.\-]+)(?::(\w+))?\s*$")
    for line in proc.stdout.splitlines():
        match = pattern.match(line)
        if not match:
            continue
        group, artifact, version, scope = match.groups()
        found[f"{group}:{artifact}"] = (version, "test" if scope == "test" else "runtime")
    return found


def dependencies_from_poms(root, errors):
    """Fallback: declared dependencies/plugins/parent from every pom, with ${property} resolution."""
    found = {}
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in {".git", "target", "node_modules"}]
        if "pom.xml" not in filenames:
            continue
        path = os.path.join(dirpath, "pom.xml")
        try:
            tree = ET.parse(path)
        except ET.ParseError as exc:
            errors.append(f"{os.path.relpath(path, root)}: {exc}")
            continue
        pom = tree.getroot()
        properties = {
            child.tag.split("}")[-1]: (child.text or "").strip()
            for child in pom.findall("m:properties/*", POM_NS)
        }

        def resolve(text):
            value = (text or "").strip()
            match = re.fullmatch(r"\$\{([^}]+)\}", value)
            return properties.get(match.group(1), "") if match else value

        parent = pom.find("m:parent", POM_NS)
        if parent is not None:
            key = f"{resolve(parent.findtext('m:groupId', '', POM_NS))}:{resolve(parent.findtext('m:artifactId', '', POM_NS))}"
            version = resolve(parent.findtext("m:version", "", POM_NS))
            if version and key not in found:
                found[key] = (version, "build_parent")
        for xpath, exposure in (
            ("m:dependencies/m:dependency", "runtime"),
            ("m:dependencyManagement/m:dependencies/m:dependency", "runtime"),
            ("m:build/m:plugins/m:plugin", "plugin"),
            ("m:build/m:pluginManagement/m:plugins/m:plugin", "plugin"),
        ):
            for node in pom.findall(xpath, POM_NS):
                group = resolve(node.findtext("m:groupId", "", POM_NS)) or "org.apache.maven.plugins"
                artifact = resolve(node.findtext("m:artifactId", "", POM_NS))
                version = resolve(node.findtext("m:version", "", POM_NS))
                scope = (node.findtext("m:scope", "", POM_NS) or "").strip()
                if not artifact or not version or "${" in version:
                    continue
                key = f"{group}:{artifact}"
                if key not in found:
                    found[key] = (version, "test" if scope == "test" else exposure)
    return found


def local_artifacts(root):
    """group:artifact of every module in this build, so the scanner never looks itself up."""
    local = set()
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in {".git", "target", "node_modules"}]
        if "pom.xml" not in filenames:
            continue
        try:
            pom = ET.parse(os.path.join(dirpath, "pom.xml")).getroot()
        except ET.ParseError:
            continue
        group = pom.findtext("m:groupId", "", POM_NS).strip() or pom.findtext(
            "m:parent/m:groupId", "", POM_NS).strip()
        artifact = pom.findtext("m:artifactId", "", POM_NS).strip()
        if group and artifact:
            local.add(f"{group}:{artifact}")
    return local


def java_release(root, latest, errors):
    """The pom's java.version against the newest LTS feature release."""
    pom = resolve_within(root, "pom.xml")
    if not pom or not os.path.isfile(pom):
        errors.append("no root pom.xml; skipped java.version check")
        return None
    with open(pom) as handle:
        match = re.search(r"<java\.version>([^<]+)</java\.version>", handle.read())
    if not match:
        return None
    current = match.group(1).strip()
    return {
        "component": "java",
        "ecosystem": "maven",
        "kind": "runtime",
        "location": "pom.xml (<java.version>)",
        "current": current,
        "latest": latest,
        "exposure": "runtime_platform",
        "gap": version_gap(current, latest),
        "advisories": [],
    }


def latest_central_version(group, artifact, errors, cache):
    """Newest non-prerelease version published to Maven Central for a coordinate."""
    key = f"{group}:{artifact}"
    if key in cache:
        return cache[key]
    url = f"{CENTRAL_REPO}/{group.replace('.', '/')}/{artifact}/maven-metadata.xml"
    try:
        with urllib.request.urlopen(url, timeout=30) as response:
            metadata = ET.fromstring(response.read())
    except (urllib.error.URLError, ET.ParseError, OSError) as exc:
        errors.append(f"maven central metadata lookup failed for {key}: {exc}")
        cache[key] = None
        return None
    versions = [
        (node.text or "").strip()
        for node in metadata.findall("versioning/versions/version")
        if (node.text or "").strip() and not is_prerelease(node.text)
    ]
    latest = max(versions, key=parse_version) if versions else None
    cache[key] = latest
    return latest


def osv_advisories(coordinates, errors):
    """group:artifact -> advisories, via OSV querybatch plus per-id detail lookups."""
    queries = [
        {"package": {"name": key, "ecosystem": "Maven"}, "version": version}
        for key, (version, _) in coordinates.items()
    ]
    if not queries:
        return {}
    results = []
    for start in range(0, len(queries), 200):
        chunk = queries[start:start + 200]
        try:
            body = http_json(OSV_QUERYBATCH, {"queries": chunk}, timeout=120)
        except (urllib.error.URLError, ValueError, OSError) as exc:
            errors.append(f"osv querybatch failed: {exc}")
            return {}
        results.extend(body.get("results") or [])

    details = {}
    advisories = {}
    for key, result in zip(coordinates, results):
        entries = []
        for vuln in (result or {}).get("vulns") or []:
            vuln_id = vuln.get("id")
            if not vuln_id:
                continue
            if vuln_id not in details:
                try:
                    details[vuln_id] = http_json(OSV_VULN + vuln_id)
                except (urllib.error.URLError, ValueError, OSError) as exc:
                    errors.append(f"osv detail lookup failed for {vuln_id}: {exc}")
                    details[vuln_id] = {}
            detail = details[vuln_id]
            entries.append({
                "id": vuln_id,
                "severity": osv_severity(detail),
                "summary": (detail.get("summary") or "")[:200],
                "url": f"https://osv.dev/vulnerability/{vuln_id}",
            })
        if entries:
            advisories[key] = entries
    return advisories


def osv_severity(detail):
    labelled = ((detail.get("database_specific") or {}).get("severity") or "").upper()
    if labelled in SEVERITY_POINTS:
        return labelled
    for entry in detail.get("severity") or []:
        base = re.search(r"(\d+(?:\.\d+)?)\s*$", str(entry.get("score") or ""))
        if not base:
            continue
        value = float(base.group(1))
        return "CRITICAL" if value >= 9 else "HIGH" if value >= 7 else "MODERATE" if value >= 4 else "LOW"
    return "MODERATE"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--out", help="write JSON here instead of stdout")
    parser.add_argument("--root", default=os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
    parser.add_argument("--max-advisories", type=int, default=5,
                        help="advisories kept per component (highest severity first); the rest are counted only")
    parser.add_argument("--skip-maven", action="store_true",
                        help="skip `mvn dependency:list` and parse the poms directly")
    parser.add_argument("--latest-lts-java", default="25",
                        help="newest Java LTS feature release to compare <java.version> against")
    args = parser.parse_args()

    root = os.path.abspath(args.root)
    errors = []
    coordinates = {} if args.skip_maven else dependencies_from_maven(root, errors)
    if not coordinates:
        coordinates = dependencies_from_poms(root, errors)
    for key in local_artifacts(root):
        coordinates.pop(key, None)
    if not coordinates:
        errors.append("no Maven coordinates discovered")

    advisories = osv_advisories(coordinates, errors)
    cache = {}
    items = []
    for key, (version, exposure) in sorted(coordinates.items()):
        group, artifact = key.split(":", 1)
        latest = latest_central_version(group, artifact, errors, cache) or version
        gap = version_gap(version, latest)
        entries = advisories.get(key, [])
        if not entries and gap == {"major": 0, "minor": 0, "patch": 0}:
            continue
        items.append({
            "component": key,
            "ecosystem": "maven",
            "kind": "plugin" if exposure == "plugin" else "dependency",
            "location": "pom.xml",
            "current": version,
            "latest": latest,
            "exposure": exposure,
            "gap": gap,
            "advisories": entries,
        })

    runtime = java_release(root, args.latest_lts_java, errors)
    if runtime and runtime["gap"] != {"major": 0, "minor": 0, "patch": 0}:
        items.append(runtime)

    for item in items:
        item["rank_score"] = rank_score(item)
        item["size"] = size_estimate(item)
        item["advisory_count"] = len(item["advisories"])
        item["advisories"].sort(key=lambda a: -SEVERITY_POINTS.get(a["severity"].upper(), 4))
        del item["advisories"][args.max_advisories:]
    items.sort(key=lambda i: (-i["rank_score"], i["size"], i["component"]))

    document = {
        "repo": REPO,
        "generated_at": datetime.now(timezone.utc).isoformat(timespec="seconds"),
        "scanner": "scripts/drift_scan.py",
        "items": items,
        "errors": errors,
    }
    payload = json.dumps(document, indent=2)
    if args.out:
        with open(resolve_output(args.out), "w") as handle:
            handle.write(payload + "\n")
    else:
        print(payload)
    return 0


if __name__ == "__main__":
    sys.exit(main())
