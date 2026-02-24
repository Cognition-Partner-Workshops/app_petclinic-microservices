package org.springframework.samples.petclinic.customers.cucumber;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
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
 * Cucumber step definitions specific to the customers-service.
 * Covers owner CRUD operations, pet management, and parameterized data-driven tests.
 */
public class CustomerStepDefinitions {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScenarioContext context;

	@When("I create an owner with firstName {string} lastName {string} address {string} city {string} telephone {string}")
	public void iCreateAnOwner(String firstName, String lastName, String address, String city, String telephone)
			throws Exception {
		String body = """
			{
				"firstName": "%s",
				"lastName": "%s",
				"address": "%s",
				"city": "%s",
				"telephone": "%s"
			}
			""".formatted(firstName, lastName, address, city, telephone);

		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.post("/owners")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I create owners from CSV {string}")
	public void iCreateOwnersFromCsv(String csvPath) throws Exception {
		List<Map<String, String>> rows = TestDataLoader.loadCsv(csvPath);
		MvcResult lastResult = null;
		ResultActions lastActions = null;

		for (Map<String, String> row : rows) {
			String body = """
				{
					"firstName": "%s",
					"lastName": "%s",
					"address": "%s",
					"city": "%s",
					"telephone": "%s"
				}
				""".formatted(
				TestDataLoader.requireString(row, "firstName"),
				TestDataLoader.requireString(row, "lastName"),
				TestDataLoader.requireString(row, "address"),
				TestDataLoader.requireString(row, "city"),
				TestDataLoader.requireString(row, "telephone"));

			lastActions = mockMvc.perform(
				MockMvcRequestBuilders.post("/owners")
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

	@When("I update owner {int} with firstName {string} lastName {string} address {string} city {string} telephone {string}")
	public void iUpdateOwner(int ownerId, String firstName, String lastName, String address, String city,
			String telephone) throws Exception {
		String body = """
			{
				"firstName": "%s",
				"lastName": "%s",
				"address": "%s",
				"city": "%s",
				"telephone": "%s"
			}
			""".formatted(firstName, lastName, address, city, telephone);

		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.put("/owners/" + ownerId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I create a pet for owner {int} with name {string} birthDate {string} typeId {int}")
	public void iCreateAPetForOwner(int ownerId, String name, String birthDate, int typeId) throws Exception {
		String body = """
			{
				"id": 0,
				"name": "%s",
				"birthDate": "%s",
				"typeId": %d
			}
			""".formatted(name, birthDate, typeId);

		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.post("/owners/" + ownerId + "/pets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@Then("the response should contain owner with firstName {string}")
	public void theResponseShouldContainOwnerWithFirstName(String expectedFirstName) {
		String body = context.getLastResponseBody();
		String firstName = JsonPath.read(body, "$.firstName");
		assertThat(firstName).isEqualTo(expectedFirstName);
	}

	@Then("the owners list should have at least {int} entries")
	public void theOwnersListShouldHaveAtLeastEntries(int minSize) {
		String body = context.getLastResponseBody();
		JSONArray array = JsonPath.read(body, "$");
		assertThat(array.size()).isGreaterThanOrEqualTo(minSize);
	}

}
