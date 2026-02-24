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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.validation.config.DataValidationProperties;
import org.springframework.samples.petclinic.validation.model.ComparisonType;
import org.springframework.samples.petclinic.validation.model.FieldMismatch;
import org.springframework.samples.petclinic.validation.model.ValidationReport;
import org.springframework.samples.petclinic.validation.model.ValidationRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Comparison service for File-to-DB mode: parses a mainframe fixed-width output file
 * and compares its data against Postgres query results.
 */
@Service
public class FileToDbComparisonService {

	private final JdbcTemplate destinationJdbcTemplate;

	private final FixedWidthFileParser fileParser;

	private final RowComparator rowComparator;

	private final DataValidationProperties properties;

	public FileToDbComparisonService(@Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate,
			FixedWidthFileParser fileParser, RowComparator rowComparator, DataValidationProperties properties) {
		this.destinationJdbcTemplate = destinationJdbcTemplate;
		this.fileParser = fileParser;
		this.rowComparator = rowComparator;
		this.properties = properties;
	}

	/**
	 * Executes a File-to-DB comparison: parses the mainframe output file and compares
	 * it against data queried from the destination database.
	 * @param request the validation request containing file path, field definitions, and destination query
	 * @return a populated {@link ValidationReport}
	 */
	public ValidationReport compare(ValidationRequest request) {
		ValidationReport report = new ValidationReport();
		report.setComparisonType(ComparisonType.FILE_TO_DB);
		report.setTimestamp(Instant.now());

		try {
			String baseDir = properties.getMainframeFile().getBaseDirectory();
			Path filePath = Paths.get(baseDir, request.getFilePath());
			Charset encoding = Charset.forName(properties.getMainframeFile().getDefaultEncoding());

			List<Map<String, String>> fileRows = fileParser.parse(filePath, request.getFieldDefinitions(),
					request.getSkipHeaderLines(), encoding);

			List<Map<String, String>> dbRows = queryToStringMaps(destinationJdbcTemplate,
					request.getDestinationQuery());

			report.setSourceRowCount(fileRows.size());
			report.setDestinationRowCount(dbRows.size());
			report.setRowCountMatch(fileRows.size() == dbRows.size());

			RowComparator.ComparisonResult result = rowComparator.compare(fileRows, dbRows,
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

	private List<Map<String, String>> queryToStringMaps(JdbcTemplate jdbcTemplate, String sql) {
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			Map<String, String> row = new LinkedHashMap<>();
			int columnCount = rs.getMetaData().getColumnCount();
			for (int c = 1; c <= columnCount; c++) {
				String colName = rs.getMetaData().getColumnLabel(c).toLowerCase();
				Object val = rs.getObject(c);
				row.put(colName, val != null ? val.toString() : "");
			}
			return row;
		});
	}

}
