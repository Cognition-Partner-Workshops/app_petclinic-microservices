package org.springframework.samples.petclinic.validation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.validation.config.DataValidationProperties;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FileToApiComparisonService} API response parsing and URL resolution logic.
 */
class FileToApiComparisonServiceTest {

	private FileToApiComparisonService service;

	private DataValidationProperties properties;

	@BeforeEach
	void setUp() {
		properties = new DataValidationProperties();
		properties.getApi().setBaseUrl("http://localhost:8080");
		service = new FileToApiComparisonService(null, null, null, properties, new ObjectMapper());
	}

	@Test
	void shouldResolveRelativeEndpoint() {
		String resolved = service.resolveApiUrl("/api/owners");
		assertThat(resolved).isEqualTo("http://localhost:8080/api/owners");
	}

	@Test
	void shouldResolveRelativeEndpointWithoutLeadingSlash() {
		String resolved = service.resolveApiUrl("api/owners");
		assertThat(resolved).isEqualTo("http://localhost:8080/api/owners");
	}

	@Test
	void shouldReturnAbsoluteUrlUnchanged() {
		String resolved = service.resolveApiUrl("https://external.api.com/data");
		assertThat(resolved).isEqualTo("https://external.api.com/data");
	}

	@Test
	void shouldResolveWhenBaseUrlHasTrailingSlash() {
		properties.getApi().setBaseUrl("http://localhost:8080/");
		String resolved = service.resolveApiUrl("/api/owners");
		assertThat(resolved).isEqualTo("http://localhost:8080/api/owners");
	}

	@Test
	void shouldParseJsonArrayResponse() throws Exception {
		String json = """
				[
				  {"id": "1", "name": "Alice", "city": "NYC"},
				  {"id": "2", "name": "Bob", "city": "LA"}
				]
				""";

		List<Map<String, String>> rows = service.parseApiResponse(json, null);

		assertThat(rows).hasSize(2);
		assertThat(rows.get(0).get("id")).isEqualTo("1");
		assertThat(rows.get(0).get("name")).isEqualTo("Alice");
		assertThat(rows.get(1).get("name")).isEqualTo("Bob");
	}

	@Test
	void shouldParseNestedJsonArrayWithJsonPath() throws Exception {
		String json = """
				{
				  "status": "ok",
				  "data": {
				    "records": [
				      {"id": "1", "name": "Alice"},
				      {"id": "2", "name": "Bob"}
				    ]
				  }
				}
				""";

		List<Map<String, String>> rows = service.parseApiResponse(json, "data.records");

		assertThat(rows).hasSize(2);
		assertThat(rows.get(0).get("id")).isEqualTo("1");
		assertThat(rows.get(1).get("name")).isEqualTo("Bob");
	}

	@Test
	void shouldReturnEmptyListForMissingJsonPath() throws Exception {
		String json = """
				{"data": {"items": []}}
				""";

		List<Map<String, String>> rows = service.parseApiResponse(json, "data.records");

		assertThat(rows).isEmpty();
	}

	@Test
	void shouldHandleEmptyJsonArray() throws Exception {
		String json = "[]";

		List<Map<String, String>> rows = service.parseApiResponse(json, null);

		assertThat(rows).isEmpty();
	}

	@Test
	void shouldParseNumericValuesAsStrings() throws Exception {
		String json = """
				[{"id": 42, "balance": 1234.56, "active": true}]
				""";

		List<Map<String, String>> rows = service.parseApiResponse(json, null);

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0).get("id")).isEqualTo("42");
		assertThat(rows.get(0).get("balance")).isEqualTo("1234.56");
		assertThat(rows.get(0).get("active")).isEqualTo("true");
	}

}
