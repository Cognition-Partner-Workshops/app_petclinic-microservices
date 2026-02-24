package org.springframework.samples.petclinic.validation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.validation.model.FieldMismatch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RowComparator} field-level comparison logic.
 */
class RowComparatorTest {

	private RowComparator rowComparator;

	@BeforeEach
	void setUp() {
		rowComparator = new RowComparator();
	}

	@Test
	void shouldReportAllMatchesWhenDataIsIdentical() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1", "name", "Alice", "city", "NYC"),
			createRow("id", "2", "name", "Bob", "city", "LA")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1", "name", "Alice", "city", "NYC"),
			createRow("id", "2", "name", "Bob", "city", "LA")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("id", "name", "city"));

		assertThat(result.getTotalFieldsCompared()).isEqualTo(6);
		assertThat(result.getMatchCount()).isEqualTo(6);
		assertThat(result.getMismatchCount()).isEqualTo(0);
		assertThat(result.getMismatches()).isEmpty();
	}

	@Test
	void shouldDetectFieldLevelMismatches() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1", "name", "Alice", "city", "NYC")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1", "name", "Alicia", "city", "SF")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("id", "name", "city"));

		assertThat(result.getTotalFieldsCompared()).isEqualTo(3);
		assertThat(result.getMatchCount()).isEqualTo(1);
		assertThat(result.getMismatchCount()).isEqualTo(2);
		assertThat(result.getMismatches()).hasSize(2);

		FieldMismatch nameMismatch = result.getMismatches().get(0);
		assertThat(nameMismatch.getFieldName()).isEqualTo("name");
		assertThat(nameMismatch.getSourceValue()).isEqualTo("Alice");
		assertThat(nameMismatch.getDestinationValue()).isEqualTo("Alicia");

		FieldMismatch cityMismatch = result.getMismatches().get(1);
		assertThat(cityMismatch.getFieldName()).isEqualTo("city");
		assertThat(cityMismatch.getSourceValue()).isEqualTo("NYC");
		assertThat(cityMismatch.getDestinationValue()).isEqualTo("SF");
	}

	@Test
	void shouldHandleRowCountMismatchWithExtraSourceRows() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1", "name", "Alice"),
			createRow("id", "2", "name", "Bob"),
			createRow("id", "3", "name", "Charlie")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1", "name", "Alice")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("id", "name"));

		// 1 row compared (2 fields) + 2 extra rows (2 fields each) = 6 total
		assertThat(result.getTotalFieldsCompared()).isEqualTo(6);
		assertThat(result.getMatchCount()).isEqualTo(2);
		assertThat(result.getMismatchCount()).isEqualTo(4);
	}

	@Test
	void shouldHandleRowCountMismatchWithExtraDestRows() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1", "name", "Alice")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1", "name", "Alice"),
			createRow("id", "2", "name", "Bob")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("id", "name"));

		assertThat(result.getTotalFieldsCompared()).isEqualTo(4);
		assertThat(result.getMatchCount()).isEqualTo(2);
		assertThat(result.getMismatchCount()).isEqualTo(2);
	}

	@Test
	void shouldCompareAllFieldsWhenCompareFieldsIsNull() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1", "name", "Alice", "city", "NYC")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1", "name", "Alice", "city", "NYC")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, null);

		assertThat(result.getTotalFieldsCompared()).isEqualTo(3);
		assertThat(result.getMatchCount()).isEqualTo(3);
		assertThat(result.getMismatchCount()).isEqualTo(0);
	}

	@Test
	void shouldCompareAllFieldsWhenCompareFieldsIsEmpty() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1", "name", "Alice")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1", "name", "Bob")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of());

		assertThat(result.getTotalFieldsCompared()).isEqualTo(2);
		assertThat(result.getMatchCount()).isEqualTo(1);
		assertThat(result.getMismatchCount()).isEqualTo(1);
	}

	@Test
	void shouldHandleEmptyDatasets() {
		List<Map<String, String>> source = List.of();
		List<Map<String, String>> dest = List.of();

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("id"));

		assertThat(result.getTotalFieldsCompared()).isEqualTo(0);
		assertThat(result.getMatchCount()).isEqualTo(0);
		assertThat(result.getMismatchCount()).isEqualTo(0);
		assertThat(result.getMismatches()).isEmpty();
	}

	@Test
	void shouldNormalizeTrimWhitespace() {
		List<Map<String, String>> source = List.of(
			createRow("name", "  Alice  ")
		);
		List<Map<String, String>> dest = List.of(
			createRow("name", "Alice")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("name"));

		assertThat(result.getMatchCount()).isEqualTo(1);
		assertThat(result.getMismatchCount()).isEqualTo(0);
	}

	@Test
	void shouldTreatNullValuesAsEmptyStrings() {
		Map<String, String> sourceRow = new LinkedHashMap<>();
		sourceRow.put("name", null);
		List<Map<String, String>> source = List.of(sourceRow);

		List<Map<String, String>> dest = List.of(
			createRow("name", "")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("name"));

		assertThat(result.getMatchCount()).isEqualTo(1);
		assertThat(result.getMismatchCount()).isEqualTo(0);
	}

	@Test
	void shouldTrackRowIndexInMismatches() {
		List<Map<String, String>> source = List.of(
			createRow("id", "1"),
			createRow("id", "2"),
			createRow("id", "3")
		);
		List<Map<String, String>> dest = List.of(
			createRow("id", "1"),
			createRow("id", "X"),
			createRow("id", "3")
		);

		RowComparator.ComparisonResult result = rowComparator.compare(source, dest, List.of("id"));

		assertThat(result.getMismatches()).hasSize(1);
		assertThat(result.getMismatches().get(0).getRowIndex()).isEqualTo(1);
		assertThat(result.getMismatches().get(0).getSourceValue()).isEqualTo("2");
		assertThat(result.getMismatches().get(0).getDestinationValue()).isEqualTo("X");
	}

	private Map<String, String> createRow(String... keyValues) {
		Map<String, String> row = new LinkedHashMap<>();
		for (int i = 0; i < keyValues.length; i += 2) {
			row.put(keyValues[i], keyValues[i + 1]);
		}
		return row;
	}

}
