package org.springframework.samples.petclinic.validation.cucumber;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.test.data.TestDataLoader;
import org.springframework.samples.petclinic.test.steps.ScenarioContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cucumber step definitions specific to the data-validation-service.
 * Covers validation endpoint testing for DB_TO_DB, FILE_TO_DB, and FILE_TO_API comparisons.
 */
public class ValidationStepDefinitions {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScenarioContext context;

	@When("I send a validation request of type {string} with body:")
	public void iSendAValidationRequest(String comparisonType, String body) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.post("/validate/" + comparisonType)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I send a validation request of type {string} from YAML {string} scenario {int}")
	public void iSendAValidationRequestFromYaml(String comparisonType, String yamlPath, int scenarioIndex)
			throws Exception {
		List<Map<String, Object>> scenarios = TestDataLoader.loadYaml(yamlPath);
		assertThat(scenarios.size()).as("YAML scenario index %d out of bounds", scenarioIndex)
			.isGreaterThan(scenarioIndex);

		Map<String, Object> scenario = scenarios.get(scenarioIndex);
		String body = buildValidationRequestJson(scenario);

		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.post("/validate/" + comparisonType)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@Then("the validation report status should be {string}")
	public void theValidationReportStatusShouldBe(String expectedStatus) {
		String body = context.getLastResponseBody();
		String status = JsonPath.read(body, "$.status");
		assertThat(status).isEqualTo(expectedStatus);
	}

	@Then("the validation report should have passed")
	public void theValidationReportShouldHavePassed() {
		String body = context.getLastResponseBody();
		Boolean passed = JsonPath.read(body, "$.passed");
		assertThat(passed).isTrue();
	}

	@Then("the validation report should have failed")
	public void theValidationReportShouldHaveFailed() {
		String body = context.getLastResponseBody();
		Boolean passed = JsonPath.read(body, "$.passed");
		assertThat(passed).isFalse();
	}

	@Then("the validation report should have an error message containing {string}")
	public void theValidationReportShouldHaveErrorMessageContaining(String expectedSubstring) {
		String body = context.getLastResponseBody();
		String errorMessage = JsonPath.read(body, "$.errorMessage");
		assertThat(errorMessage).contains(expectedSubstring);
	}

	@Then("the validation report match percentage should be at least {double}")
	public void theMatchPercentageShouldBeAtLeast(double minPercentage) {
		String body = context.getLastResponseBody();
		double matchPercentage = ((Number) JsonPath.read(body, "$.matchPercentage")).doubleValue();
		assertThat(matchPercentage).isGreaterThanOrEqualTo(minPercentage);
	}

	private String buildValidationRequestJson(Map<String, Object> scenario) {
		StringBuilder json = new StringBuilder("{");
		boolean first = true;

		for (Map.Entry<String, Object> entry : scenario.entrySet()) {
			if ("description".equals(entry.getKey()) || "expectedStatus".equals(entry.getKey())) {
				continue; // skip metadata fields
			}
			if (!first) {
				json.append(",");
			}
			json.append("\"").append(entry.getKey()).append("\":");
			appendJsonValue(json, entry.getValue());
			first = false;
		}

		json.append("}");
		return json.toString();
	}

	@SuppressWarnings("unchecked")
	private void appendJsonValue(StringBuilder json, Object value) {
		if (value == null) {
			json.append("null");
		}
		else if (value instanceof Number) {
			json.append(value);
		}
		else if (value instanceof Boolean) {
			json.append(value);
		}
		else if (value instanceof List) {
			json.append("[");
			boolean first = true;
			for (Object item : (List<Object>) value) {
				if (!first) {
					json.append(",");
				}
				appendJsonValue(json, item);
				first = false;
			}
			json.append("]");
		}
		else if (value instanceof Map) {
			json.append("{");
			boolean first = true;
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
				if (!first) {
					json.append(",");
				}
				json.append("\"").append(entry.getKey()).append("\":");
				appendJsonValue(json, entry.getValue());
				first = false;
			}
			json.append("}");
		}
		else {
			json.append("\"").append(value).append("\"");
		}
	}

}
