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

import org.springframework.samples.petclinic.validation.model.FieldMismatch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compares two lists of row data (represented as maps of field name to string value)
 * and produces a list of {@link FieldMismatch} entries for any differences found.
 */
@Component
public class RowComparator {

	/**
	 * Result of comparing two datasets field-by-field.
	 */
	public static class ComparisonResult {

		private final int totalFieldsCompared;

		private final int matchCount;

		private final int mismatchCount;

		private final List<FieldMismatch> mismatches;

		public ComparisonResult(int totalFieldsCompared, int matchCount, int mismatchCount,
				List<FieldMismatch> mismatches) {
			this.totalFieldsCompared = totalFieldsCompared;
			this.matchCount = matchCount;
			this.mismatchCount = mismatchCount;
			this.mismatches = mismatches;
		}

		public int getTotalFieldsCompared() {
			return totalFieldsCompared;
		}

		public int getMatchCount() {
			return matchCount;
		}

		public int getMismatchCount() {
			return mismatchCount;
		}

		public List<FieldMismatch> getMismatches() {
			return mismatches;
		}

	}

	/**
	 * Compares rows from a source dataset against rows from a destination dataset,
	 * matching rows by index and comparing all specified fields.
	 * @param sourceRows the source data rows
	 * @param destinationRows the destination data rows
	 * @param compareFields the field names to compare; if null or empty, all fields in source rows are compared
	 * @return a {@link ComparisonResult} with totals and mismatches
	 */
	public ComparisonResult compare(List<Map<String, String>> sourceRows,
			List<Map<String, String>> destinationRows, List<String> compareFields) {

		int totalFields = 0;
		int matchCount = 0;
		int mismatchCount = 0;
		List<FieldMismatch> mismatches = new ArrayList<>();

		int rowsToCompare = Math.min(sourceRows.size(), destinationRows.size());

		for (int i = 0; i < rowsToCompare; i++) {
			Map<String, String> sourceRow = sourceRows.get(i);
			Map<String, String> destRow = destinationRows.get(i);

			List<String> fields = (compareFields != null && !compareFields.isEmpty())
					? compareFields : new ArrayList<>(sourceRow.keySet());

			for (String field : fields) {
				totalFields++;
				String sourceVal = normalizeValue(sourceRow.get(field));
				String destVal = normalizeValue(destRow.get(field));

				if (Objects.equals(sourceVal, destVal)) {
					matchCount++;
				}
				else {
					mismatchCount++;
					mismatches.add(new FieldMismatch(i, field, sourceVal, destVal));
				}
			}
		}

		// Count remaining rows in the longer dataset as mismatches
		if (sourceRows.size() != destinationRows.size()) {
			int extraRows = Math.abs(sourceRows.size() - destinationRows.size());
			List<String> fields = (!sourceRows.isEmpty())
					? determineFields(sourceRows.get(0), compareFields)
					: (!destinationRows.isEmpty()
							? determineFields(destinationRows.get(0), compareFields)
							: List.of());
			int extraFieldMismatches = extraRows * fields.size();
			totalFields += extraFieldMismatches;
			mismatchCount += extraFieldMismatches;
		}

		return new ComparisonResult(totalFields, matchCount, mismatchCount, mismatches);
	}

	private List<String> determineFields(Map<String, String> sampleRow, List<String> compareFields) {
		if (compareFields != null && !compareFields.isEmpty()) {
			return compareFields;
		}
		return new ArrayList<>(sampleRow.keySet());
	}

	private String normalizeValue(String value) {
		if (value == null) {
			return "";
		}
		return value.trim();
	}

}
