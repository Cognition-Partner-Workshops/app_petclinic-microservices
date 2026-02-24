package org.springframework.samples.petclinic.validation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.samples.petclinic.validation.model.FieldDefinition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FixedWidthFileParser} mainframe file parsing logic.
 */
class FixedWidthFileParserTest {

	private FixedWidthFileParser parser;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		parser = new FixedWidthFileParser();
	}

	@Test
	void shouldParseFixedWidthFileCorrectly() throws IOException {
		// Fixed-width layout: id (0-4), name (5-20), city (21-36)
		String content = """
				HEADER LINE - SKIP THIS
				0001 Alice           New York        \s
				0002 Bob             Los Angeles     \s
				""";
		Path file = tempDir.resolve("test_data.txt");
		Files.writeString(file, content, StandardCharsets.UTF_8);

		List<FieldDefinition> fields = List.of(
			new FieldDefinition("id", 0, 5, "id"),
			new FieldDefinition("name", 5, 16, "name"),
			new FieldDefinition("city", 21, 16, "city")
		);

		List<Map<String, String>> rows = parser.parse(file, fields, 1, StandardCharsets.UTF_8);

		assertThat(rows).hasSize(2);
		assertThat(rows.get(0).get("id")).isEqualTo("0001");
		assertThat(rows.get(0).get("name")).isEqualTo("Alice");
		assertThat(rows.get(0).get("city")).isEqualTo("New York");
		assertThat(rows.get(1).get("id")).isEqualTo("0002");
		assertThat(rows.get(1).get("name")).isEqualTo("Bob");
		assertThat(rows.get(1).get("city")).isEqualTo("Los Angeles");
	}

	@Test
	void shouldSkipBlankLines() throws IOException {
		String content = """
				0001 Alice          \s

				0002 Bob            \s
				""";
		Path file = tempDir.resolve("blanks.txt");
		Files.writeString(file, content, StandardCharsets.UTF_8);

		List<FieldDefinition> fields = List.of(
			new FieldDefinition("id", 0, 5, "id"),
			new FieldDefinition("name", 5, 16, "name")
		);

		List<Map<String, String>> rows = parser.parse(file, fields, 0, StandardCharsets.UTF_8);

		assertThat(rows).hasSize(2);
	}

	@Test
	void shouldSkipMultipleHeaderLines() throws IOException {
		String content = """
				==== REPORT HEADER ====
				Date: 2024-01-01
				0001 Alice          \s
				""";
		Path file = tempDir.resolve("multi_header.txt");
		Files.writeString(file, content, StandardCharsets.UTF_8);

		List<FieldDefinition> fields = List.of(
			new FieldDefinition("id", 0, 5, "id"),
			new FieldDefinition("name", 5, 16, "name")
		);

		List<Map<String, String>> rows = parser.parse(file, fields, 2, StandardCharsets.UTF_8);

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0).get("id")).isEqualTo("0001");
	}

	@Test
	void shouldHandleShortLinesGracefully() throws IOException {
		// Line is shorter than the field definition expects
		String content = "001 A\n";
		Path file = tempDir.resolve("short.txt");
		Files.writeString(file, content, StandardCharsets.UTF_8);

		List<FieldDefinition> fields = List.of(
			new FieldDefinition("id", 0, 4, "id"),
			new FieldDefinition("name", 4, 10, "name"),
			new FieldDefinition("city", 14, 10, "city")
		);

		List<Map<String, String>> rows = parser.parse(file, fields, 0, StandardCharsets.UTF_8);

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0).get("id")).isEqualTo("001");
		assertThat(rows.get(0).get("name")).isEqualTo("A");
		assertThat(rows.get(0).get("city")).isEqualTo("");
	}

	@Test
	void shouldUseTargetFieldNameWhenSet() throws IOException {
		String content = "0001 Alice\n";
		Path file = tempDir.resolve("target_name.txt");
		Files.writeString(file, content, StandardCharsets.UTF_8);

		List<FieldDefinition> fields = List.of(
			new FieldDefinition("ACCT_ID", 0, 5, "account_id"),
			new FieldDefinition("CUST_NM", 5, 16, "customer_name")
		);

		List<Map<String, String>> rows = parser.parse(file, fields, 0, StandardCharsets.UTF_8);

		assertThat(rows.get(0)).containsKey("account_id");
		assertThat(rows.get(0)).containsKey("customer_name");
		assertThat(rows.get(0).get("account_id")).isEqualTo("0001");
	}

	@Test
	void shouldParseLineCorrectly() {
		String line = "0001 Alice           New York        ";
		List<FieldDefinition> fields = List.of(
			new FieldDefinition("id", 0, 5, "id"),
			new FieldDefinition("name", 5, 16, "name"),
			new FieldDefinition("city", 21, 16, "city")
		);

		Map<String, String> row = parser.parseLine(line, fields);

		assertThat(row.get("id")).isEqualTo("0001");
		assertThat(row.get("name")).isEqualTo("Alice");
		assertThat(row.get("city")).isEqualTo("New York");
	}

	@Test
	void shouldHandleEmptyFile() throws IOException {
		Path file = tempDir.resolve("empty.txt");
		Files.writeString(file, "", StandardCharsets.UTF_8);

		List<FieldDefinition> fields = List.of(
			new FieldDefinition("id", 0, 5, "id")
		);

		List<Map<String, String>> rows = parser.parse(file, fields, 0, StandardCharsets.UTF_8);

		assertThat(rows).isEmpty();
	}

}
