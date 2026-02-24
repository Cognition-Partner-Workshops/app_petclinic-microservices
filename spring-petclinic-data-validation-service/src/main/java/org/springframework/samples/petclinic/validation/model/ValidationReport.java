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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The result of a data validation comparison, containing match/mismatch counts,
 * sample mismatches, and an overall pass/fail status.
 */
public class ValidationReport {

	private ComparisonType comparisonType;

	private Instant timestamp;

	private String status;

	private int sourceRowCount;

	private int destinationRowCount;

	private boolean rowCountMatch;

	private int totalFieldsCompared;

	private int matchCount;

	private int mismatchCount;

	private List<FieldMismatch> sampleMismatches = new ArrayList<>();

	private double matchPercentage;

	private boolean passed;

	private String errorMessage;

	public ComparisonType getComparisonType() {
		return comparisonType;
	}

	public void setComparisonType(ComparisonType comparisonType) {
		this.comparisonType = comparisonType;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSourceRowCount() {
		return sourceRowCount;
	}

	public void setSourceRowCount(int sourceRowCount) {
		this.sourceRowCount = sourceRowCount;
	}

	public int getDestinationRowCount() {
		return destinationRowCount;
	}

	public void setDestinationRowCount(int destinationRowCount) {
		this.destinationRowCount = destinationRowCount;
	}

	public boolean isRowCountMatch() {
		return rowCountMatch;
	}

	public void setRowCountMatch(boolean rowCountMatch) {
		this.rowCountMatch = rowCountMatch;
	}

	public int getTotalFieldsCompared() {
		return totalFieldsCompared;
	}

	public void setTotalFieldsCompared(int totalFieldsCompared) {
		this.totalFieldsCompared = totalFieldsCompared;
	}

	public int getMatchCount() {
		return matchCount;
	}

	public void setMatchCount(int matchCount) {
		this.matchCount = matchCount;
	}

	public int getMismatchCount() {
		return mismatchCount;
	}

	public void setMismatchCount(int mismatchCount) {
		this.mismatchCount = mismatchCount;
	}

	public List<FieldMismatch> getSampleMismatches() {
		return sampleMismatches;
	}

	public void setSampleMismatches(List<FieldMismatch> sampleMismatches) {
		this.sampleMismatches = sampleMismatches;
	}

	public double getMatchPercentage() {
		return matchPercentage;
	}

	public void setMatchPercentage(double matchPercentage) {
		this.matchPercentage = matchPercentage;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
