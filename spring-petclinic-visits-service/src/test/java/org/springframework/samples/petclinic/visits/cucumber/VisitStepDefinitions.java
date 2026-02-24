package org.springframework.samples.petclinic.visits.cucumber;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.minidev.json.JSONArray;
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
 * Cucumber step definitions specific to the visits-service.
 * Covers visit creation, retrieval by pet, and data-driven visit tests.
 */
public class VisitStepDefinitions {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScenarioContext context;

	@When("I create a visit for pet {int} with date {string} and description {string}")
	public void iCreateAVisitForPet(int petId, String date, String description) throws Exception {
		String body = """
			{
				"date": "%s",
				"description": "%s"
			}
			""".formatted(date, description);

		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.post("/owners/1/pets/" + petId + "/visits")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I create visits from CSV {string} for pet {int}")
	public void iCreateVisitsFromCsv(String csvPath, int petId) throws Exception {
		List<Map<String, String>> rows = TestDataLoader.loadCsv(csvPath);
		MvcResult lastResult = null;
		ResultActions lastActions = null;

		for (Map<String, String> row : rows) {
			String body = """
				{
					"date": "%s",
					"description": "%s"
				}
				""".formatted(
				TestDataLoader.requireString(row, "date"),
				TestDataLoader.requireString(row, "description"));

			lastActions = mockMvc.perform(
				MockMvcRequestBuilders.post("/owners/1/pets/" + petId + "/visits")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body)
					.accept(MediaType.APPLICATION_JSON));
			lastResult = lastActions.andReturn();
			lastActions.andExpect(status().isCreated());
		}

		if (lastResult != null) {
			context.setLastResult(lastResult);
			context.setLastResultActions(lastActions);
		}
	}

	@When("I get visits for pet {int}")
	public void iGetVisitsForPet(int petId) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/owners/1/pets/" + petId + "/visits")
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I get visits for pets {string}")
	public void iGetVisitsForPets(String petIds) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/pets/visits")
				.param("petId", petIds.split(","))
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@Then("the visits list should have at least {int} entries")
	public void theVisitsListShouldHaveAtLeastEntries(int minSize) {
		String body = context.getLastResponseBody();
		JSONArray array = JsonPath.read(body, "$");
		assertThat(array.size()).isGreaterThanOrEqualTo(minSize);
	}

	@Then("the visit should have description {string}")
	public void theVisitShouldHaveDescription(String expectedDescription) {
		String body = context.getLastResponseBody();
		String description = JsonPath.read(body, "$.description");
		assertThat(description).isEqualTo(expectedDescription);
	}

	@Then("the visits items should not be empty")
	public void theVisitsItemsShouldNotBeEmpty() {
		String body = context.getLastResponseBody();
		JSONArray items = JsonPath.read(body, "$.items");
		assertThat(items).isNotEmpty();
	}

}
