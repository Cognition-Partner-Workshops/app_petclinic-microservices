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
package org.springframework.samples.petclinic.validation.model;

import java.util.List;

/**
 * Request payload for a validation comparison.
 * Contains all parameters needed to execute a comparison of the specified type.
 */
public class ValidationRequest {

	// --- DB-to-DB fields ---

	/**
	 * SQL query to run against the source (DB2/mainframe) database.
	 */
	private String sourceQuery;

	/**
	 * SQL query to run against the destination (Postgres) database.
	 */
	private String destinationQuery;

	/**
	 * Ordered list of column names to compare between source and destination result sets.
	 */
	private List<String> compareFields;

	// --- File-based fields ---

	/**
	 * Relative path (under the configured base directory) to the mainframe output file.
	 */
	private String filePath;

	/**
	 * Field definitions describing the fixed-width layout of the mainframe file.
	 */
	private List<FieldDefinition> fieldDefinitions;

	/**
	 * Number of header lines to skip in the mainframe file.
	 */
	private int skipHeaderLines;

	// --- File-to-API fields ---

	/**
	 * The REST API endpoint URL to call for the File-to-API comparison.
	 */
	private String apiEndpoint;

	/**
	 * JSON path expression to extract the array of records from the API response.
	 */
	private String responseJsonPath;

	// --- Threshold override ---

	/**
	 * Optional override for the pass threshold percentage.
	 * If null, the global default from application.yml is used.
	 */
	private Double passThresholdPercent;

	public String getSourceQuery() {
		return sourceQuery;
	}

	public void setSourceQuery(String sourceQuery) {
		this.sourceQuery = sourceQuery;
	}

	public String getDestinationQuery() {
		return destinationQuery;
	}

	public void setDestinationQuery(String destinationQuery) {
		this.destinationQuery = destinationQuery;
	}

	public List<String> getCompareFields() {
		return compareFields;
	}

	public void setCompareFields(List<String> compareFields) {
		this.compareFields = compareFields;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<FieldDefinition> getFieldDefinitions() {
		return fieldDefinitions;
	}

	public void setFieldDefinitions(List<FieldDefinition> fieldDefinitions) {
		this.fieldDefinitions = fieldDefinitions;
	}

	public int getSkipHeaderLines() {
		return skipHeaderLines;
	}

	public void setSkipHeaderLines(int skipHeaderLines) {
		this.skipHeaderLines = skipHeaderLines;
	}

	public String getApiEndpoint() {
		return apiEndpoint;
	}

	public void setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}

	public String getResponseJsonPath() {
		return responseJsonPath;
	}

	public void setResponseJsonPath(String responseJsonPath) {
		this.responseJsonPath = responseJsonPath;
	}

	public Double getPassThresholdPercent() {
		return passThresholdPercent;
	}

	public void setPassThresholdPercent(Double passThresholdPercent) {
		this.passThresholdPercent = passThresholdPercent;
	}

}
