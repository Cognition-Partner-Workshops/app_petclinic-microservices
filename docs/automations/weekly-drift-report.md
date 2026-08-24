# Weekly drift report — this repository's part

A weekly Devin Automation scans this repository together with `timesheet-app` and
`timesheet-infra` and publishes one report plus a ranked, sized remediation queue as a
GitHub issue in `Cognition-Partner-Workshops/timesheet-app` (label `drift-report`).

The full design — trigger, ranking formula, sizing rubric, queue contract for the Track 2
fan-out, guardrails and human checkpoint — lives in
`timesheet-app/docs/automations/weekly-drift-report.md`. This file documents only what the
automation does *here*.

## Scanner

[`scripts/drift_scan.py`](../../scripts/drift_scan.py) emits one normalized JSON document
(schema: `timesheet-app/docs/automations/remediation-queue.schema.json`):

```bash
python3 scripts/drift_scan.py --out /tmp/drift-petclinic.json   # uses ./mvnw dependency:list
python3 scripts/drift_scan.py --skip-maven                      # parse the poms directly
```

What it collects across the aggregator and every `spring-petclinic-*` module:

- declared dependency, `dependencyManagement`, plugin and parent versions — from
  `./mvnw dependency:list`, falling back to parsing every `pom.xml` (with `${property}`
  resolution) when Maven cannot resolve; the fallback is recorded under `errors`
- the newest non-prerelease release per coordinate, from Maven Central
  `maven-metadata.xml` (modules of this build are excluded, so it never looks itself up)
- known advisories per coordinate from OSV.dev (`querybatch` + per-ID severity lookup)
- `<java.version>` against the newest Java LTS (`--latest-lts-java`, default `25`)

`spring-boot-starter-parent`, `spring-cloud-dependencies`, `spring-ai-bom` and the Java
release are treated as high blast radius: a major bump there ripples through every module,
so those items are sized `L` and never batched with ordinary upgrades.

## Guardrails here

- Read-only: never edits a pom, never runs `versions:*` or any plugin that rewrites
  versions, never publishes artifacts.
- No branches, commits or PRs are created by the report run.
- Every failed Maven Central or OSV lookup is reported as a scan error, so an unreachable
  API is never presented as "no drift".
- Recommendations name concrete released versions — no `latest`, no snapshots, no
  prereleases.

## Human checkpoint

The automation stops at the published issue. Upgrades in this repository happen only after
a maintainer picks queue rows; nothing here is upgraded automatically.
