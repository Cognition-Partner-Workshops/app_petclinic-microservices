/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.validation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.samples.petclinic.validation.config.DataValidationProperties;
import org.springframework.samples.petclinic.validation.model.ComparisonType;
import org.springframework.samples.petclinic.validation.model.FieldMismatch;
import org.springframework.samples.petclinic.validation.model.ValidationReport;
import org.springframework.samples.petclinic.validation.model.ValidationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Comparison service for File-to-API mode: parses a mainframe fixed-width output file
 * and compares its data against a microservice REST API response.
 */
@Service
public class FileToApiComparisonService {

	private final RestTemplate restTemplate;

	private final FixedWidthFileParser fileParser;

	private final RowComparator rowComparator;

	private final DataValidationProperties properties;

	private final ObjectMapper objectMapper;

	public FileToApiComparisonService(RestTemplate restTemplate, FixedWidthFileParser fileParser,
			RowComparator rowComparator, DataValidationProperties properties, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.fileParser = fileParser;
		this.rowComparator = rowComparator;
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	/**
	 * Executes a File-to-API comparison: parses the mainframe output file and compares
	 * it against data from a REST API endpoint.
	 * @param request the validation request containing file path, field definitions, API endpoint, and JSON path
	 * @return a populated {@link ValidationReport}
	 */
	public ValidationReport compare(ValidationRequest request) {
		ValidationReport report = new ValidationReport();
		report.setComparisonType(ComparisonType.FILE_TO_API);
		report.setTimestamp(Instant.now());

		try {
			String baseDir = properties.getMainframeFile().getBaseDirectory();
			Path filePath = Paths.get(baseDir, request.getFilePath());
			Charset encoding = Charset.forName(properties.getMainframeFile().getDefaultEncoding());

			List<Map<String, String>> fileRows = fileParser.parse(filePath, request.getFieldDefinitions(),
					request.getSkipHeaderLines(), encoding);

			String apiUrl = resolveApiUrl(request.getApiEndpoint());
			String responseBody = restTemplate.getForObject(apiUrl, String.class);
			List<Map<String, String>> apiRows = parseApiResponse(responseBody, request.getResponseJsonPath());

			report.setSourceRowCount(fileRows.size());
			report.setDestinationRowCount(apiRows.size());
			report.setRowCountMatch(fileRows.size() == apiRows.size());

			RowComparator.ComparisonResult result = rowComparator.compare(fileRows, apiRows,
					request.getCompareFields());

			report.setTotalFieldsCompared(result.getTotalFieldsCompared());
			report.setMatchCount(result.getMatchCount());
			report.setMismatchCount(result.getMismatchCount());

			int maxSamples = properties.getMaxSampleMismatches();
			List<FieldMismatch> samples = result.getMismatches().size() > maxSamples
					? result.getMismatches().subList(0, maxSamples) : result.getMismatches();
			report.setSampleMismatches(samples);

			double matchPct = result.getTotalFieldsCompared() > 0
					? (result.getMatchCount() * 100.0) / result.getTotalFieldsCompared() : 100.0;
			report.setMatchPercentage(matchPct);

			double threshold = request.getPassThresholdPercent() != null ? request.getPassThresholdPercent()
					: properties.getPassThresholdPercent();
			report.setPassed(matchPct >= threshold);
			report.setStatus(report.isPassed() ? "PASS" : "FAIL");
		}
		catch (Exception ex) {
			report.setStatus("ERROR");
			report.setPassed(false);
			report.setErrorMessage(ex.getMessage());
		}

		return report;
	}

	/**
	 * Resolves the full API URL. If the endpoint is a relative path, it is
	 * appended to the configured base URL.
	 */
	String resolveApiUrl(String endpoint) {
		if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
			return endpoint;
		}
		String baseUrl = properties.getApi().getBaseUrl();
		if (baseUrl.endsWith("/") && endpoint.startsWith("/")) {
			return baseUrl + endpoint.substring(1);
		}
		if (!baseUrl.endsWith("/") && !endpoint.startsWith("/")) {
			return baseUrl + "/" + endpoint;
		}
		return baseUrl + endpoint;
	}

	/**
	 * Parses a JSON API response into a list of string-valued maps.
	 * Supports a simple dot-notation JSON path to locate the array of records
	 * (e.g., "data.records" navigates into {"data":{"records":[...]}}).
	 * If responseJsonPath is null or empty, the root is expected to be an array.
	 */
	List<Map<String, String>> parseApiResponse(String responseBody, String responseJsonPath) throws Exception {
		JsonNode root = objectMapper.readTree(responseBody);

		JsonNode target = root;
		if (responseJsonPath != null && !responseJsonPath.isBlank()) {
			String[] parts = responseJsonPath.split("\\.");
			for (String part : parts) {
				target = target.path(part);
			}
		}

		List<Map<String, String>> rows = new ArrayList<>();
		if (target.isArray()) {
			for (JsonNode element : target) {
				Map<String, String> row = new LinkedHashMap<>();
				Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
				while (fields.hasNext()) {
					Map.Entry<String, JsonNode> field = fields.next();
					row.put(field.getKey(), field.getValue().asText());
				}
				rows.add(row);
			}
		}

		return rows;
	}

}
