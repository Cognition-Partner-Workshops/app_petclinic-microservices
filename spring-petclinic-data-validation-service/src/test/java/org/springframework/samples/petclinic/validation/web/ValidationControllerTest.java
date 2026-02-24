package org.springframework.samples.petclinic.validation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.validation.model.ComparisonType;
import org.springframework.samples.petclinic.validation.model.ValidationReport;
import org.springframework.samples.petclinic.validation.model.ValidationRequest;
import org.springframework.samples.petclinic.validation.service.DbToDbComparisonService;
import org.springframework.samples.petclinic.validation.service.FileToApiComparisonService;
import org.springframework.samples.petclinic.validation.service.FileToDbComparisonService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ValidationController} REST endpoint.
 */
@WebMvcTest(ValidationController.class)
@ActiveProfiles("test")
class ValidationControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	DbToDbComparisonService dbToDbService;

	@MockitoBean
	FileToDbComparisonService fileToDbService;

	@MockitoBean
	FileToApiComparisonService fileToApiService;

	@Test
	void shouldReturnDbToDbValidationReport() throws Exception {
		ValidationReport report = createPassingReport(ComparisonType.DB_TO_DB);
		given(dbToDbService.compare(any(ValidationRequest.class))).willReturn(report);

		ValidationRequest request = new ValidationRequest();
		request.setSourceQuery("SELECT * FROM accounts");
		request.setDestinationQuery("SELECT * FROM accounts");
		request.setCompareFields(List.of("id", "name"));

		mvc.perform(post("/validate/DB_TO_DB")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.comparisonType").value("DB_TO_DB"))
			.andExpect(jsonPath("$.status").value("PASS"))
			.andExpect(jsonPath("$.passed").value(true))
			.andExpect(jsonPath("$.matchPercentage").value(100.0));
	}

	@Test
	void shouldReturnFileToDbValidationReport() throws Exception {
		ValidationReport report = createPassingReport(ComparisonType.FILE_TO_DB);
		given(fileToDbService.compare(any(ValidationRequest.class))).willReturn(report);

		ValidationRequest request = new ValidationRequest();
		request.setFilePath("accounts.dat");
		request.setDestinationQuery("SELECT * FROM accounts");

		mvc.perform(post("/validate/FILE_TO_DB")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.comparisonType").value("FILE_TO_DB"))
			.andExpect(jsonPath("$.passed").value(true));
	}

	@Test
	void shouldReturnFileToApiValidationReport() throws Exception {
		ValidationReport report = createPassingReport(ComparisonType.FILE_TO_API);
		given(fileToApiService.compare(any(ValidationRequest.class))).willReturn(report);

		ValidationRequest request = new ValidationRequest();
		request.setFilePath("accounts.dat");
		request.setApiEndpoint("/api/accounts");

		mvc.perform(post("/validate/FILE_TO_API")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.comparisonType").value("FILE_TO_API"))
			.andExpect(jsonPath("$.passed").value(true));
	}

	@Test
	void shouldAcceptHyphenatedComparisonType() throws Exception {
		ValidationReport report = createPassingReport(ComparisonType.DB_TO_DB);
		given(dbToDbService.compare(any(ValidationRequest.class))).willReturn(report);

		ValidationRequest request = new ValidationRequest();
		request.setSourceQuery("SELECT 1");
		request.setDestinationQuery("SELECT 1");

		mvc.perform(post("/validate/db-to-db")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.comparisonType").value("DB_TO_DB"));
	}

	@Test
	void shouldReturnBadRequestForUnknownComparisonType() throws Exception {
		ValidationRequest request = new ValidationRequest();

		mvc.perform(post("/validate/UNKNOWN_TYPE")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("ERROR"))
			.andExpect(jsonPath("$.passed").value(false))
			.andExpect(jsonPath("$.errorMessage").value(
				"Unknown comparison type: 'UNKNOWN_TYPE'. Supported types: DB_TO_DB, FILE_TO_DB, FILE_TO_API"));
	}

	@Test
	void shouldReturnInternalServerErrorWhenServiceReturnsError() throws Exception {
		ValidationReport errorReport = new ValidationReport();
		errorReport.setComparisonType(ComparisonType.DB_TO_DB);
		errorReport.setTimestamp(Instant.now());
		errorReport.setStatus("ERROR");
		errorReport.setPassed(false);
		errorReport.setErrorMessage("Connection refused");

		given(dbToDbService.compare(any(ValidationRequest.class))).willReturn(errorReport);

		ValidationRequest request = new ValidationRequest();
		request.setSourceQuery("SELECT * FROM missing_table");

		mvc.perform(post("/validate/DB_TO_DB")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.status").value("ERROR"))
			.andExpect(jsonPath("$.errorMessage").value("Connection refused"));
	}

	private ValidationReport createPassingReport(ComparisonType type) {
		ValidationReport report = new ValidationReport();
		report.setComparisonType(type);
		report.setTimestamp(Instant.now());
		report.setStatus("PASS");
		report.setSourceRowCount(10);
		report.setDestinationRowCount(10);
		report.setRowCountMatch(true);
		report.setTotalFieldsCompared(30);
		report.setMatchCount(30);
		report.setMismatchCount(0);
		report.setMatchPercentage(100.0);
		report.setPassed(true);
		return report;
	}

}
