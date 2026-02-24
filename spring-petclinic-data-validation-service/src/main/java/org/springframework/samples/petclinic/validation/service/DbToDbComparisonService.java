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
import org.springframework.samples.petclinic.validation.model.ValidationReport;
import org.springframework.samples.petclinic.validation.model.ValidationRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Comparison service for DB-to-DB mode: queries both source (DB2/mainframe) and
 * destination (Postgres) databases and compares row counts plus field-level values.
 */
@Service
public class DbToDbComparisonService {

	private final JdbcTemplate sourceJdbcTemplate;

	private final JdbcTemplate destinationJdbcTemplate;

	private final RowComparator rowComparator;

	private final DataValidationProperties properties;

	public DbToDbComparisonService(@Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate,
			@Qualifier("destinationJdbcTemplate") JdbcTemplate destinationJdbcTemplate,
			RowComparator rowComparator, DataValidationProperties properties) {
		this.sourceJdbcTemplate = sourceJdbcTemplate;
		this.destinationJdbcTemplate = destinationJdbcTemplate;
		this.rowComparator = rowComparator;
		this.properties = properties;
	}

	/**
	 * Executes a DB-to-DB comparison: queries source and destination databases,
	 * compares row counts and field-level values, and returns a validation report.
	 * @param request the validation request containing source/destination queries and fields to compare
	 * @return a populated {@link ValidationReport}
	 */
	public ValidationReport compare(ValidationRequest request) {
		ValidationReport report = new ValidationReport();
		report.setComparisonType(ComparisonType.DB_TO_DB);
		report.setTimestamp(Instant.now());

		try {
			List<Map<String, String>> sourceRows = queryToStringMaps(sourceJdbcTemplate, request.getSourceQuery());
			List<Map<String, String>> destRows = queryToStringMaps(destinationJdbcTemplate,
					request.getDestinationQuery());

			report.setSourceRowCount(sourceRows.size());
			report.setDestinationRowCount(destRows.size());
			report.setRowCountMatch(sourceRows.size() == destRows.size());

			RowComparator.ComparisonResult result = rowComparator.compare(sourceRows, destRows,
					request.getCompareFields());

			report.setTotalFieldsCompared(result.getTotalFieldsCompared());
			report.setMatchCount(result.getMatchCount());
			report.setMismatchCount(result.getMismatchCount());

			int maxSamples = properties.getMaxSampleMismatches();
			List<org.springframework.samples.petclinic.validation.model.FieldMismatch> samples = result.getMismatches()
				.size() > maxSamples ? result.getMismatches().subList(0, maxSamples) : result.getMismatches();
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
	 * Executes a SQL query and converts the result set rows into a list of string-valued maps.
	 */
	List<Map<String, String>> queryToStringMaps(JdbcTemplate jdbcTemplate, String sql) {
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
