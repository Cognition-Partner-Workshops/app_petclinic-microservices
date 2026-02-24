package org.springframework.samples.petclinic.vets.cucumber;

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

/**
 * Cucumber step definitions specific to the vets-service.
 * Covers vet listing, specialty verification, and data-driven vet validation.
 */
public class VetStepDefinitions {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScenarioContext context;

	@When("I request the list of veterinarians")
	public void iRequestTheListOfVeterinarians() throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/vets")
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@Then("the vets list should have at least {int} entries")
	public void theVetsListShouldHaveAtLeastEntries(int minSize) {
		String body = context.getLastResponseBody();
		JSONArray array = JsonPath.read(body, "$");
		assertThat(array.size()).isGreaterThanOrEqualTo(minSize);
	}

	@Then("vet at index {int} should have firstName {string} and lastName {string}")
	public void vetAtIndexShouldHaveNames(int index, String firstName, String lastName) {
		String body = context.getLastResponseBody();
		String actualFirst = JsonPath.read(body, "$[" + index + "].firstName");
		String actualLast = JsonPath.read(body, "$[" + index + "].lastName");
		assertThat(actualFirst).isEqualTo(firstName);
		assertThat(actualLast).isEqualTo(lastName);
	}

	@Then("the vets list should contain a vet named {string} {string}")
	public void theVetsListShouldContainVetNamed(String firstName, String lastName) {
		String body = context.getLastResponseBody();
		JSONArray firstNames = JsonPath.read(body, "$[?(@.firstName=='" + firstName + "' && @.lastName=='" + lastName + "')]");
		assertThat(firstNames).as("Expected vet '%s %s' in list", firstName, lastName).isNotEmpty();
	}

	@Then("vet {string} {string} should have specialty {string}")
	public void vetShouldHaveSpecialty(String firstName, String lastName, String specialty) {
		String body = context.getLastResponseBody();
		JSONArray specialties = JsonPath.read(body,
			"$[?(@.firstName=='" + firstName + "' && @.lastName=='" + lastName + "')].specialties[*].name");
		assertThat(specialties).as("Expected specialty '%s' for vet '%s %s'", specialty, firstName, lastName)
			.contains(specialty);
	}

	@Then("vet {string} {string} should have no specialties")
	public void vetShouldHaveNoSpecialties(String firstName, String lastName) {
		String body = context.getLastResponseBody();
		JSONArray specialties = JsonPath.read(body,
			"$[?(@.firstName=='" + firstName + "' && @.lastName=='" + lastName + "')].specialties[*]");
		assertThat(specialties).as("Expected no specialties for vet '%s %s'", firstName, lastName).isEmpty();
	}

	@Then("all vets from CSV {string} should be present")
	public void allVetsFromCsvShouldBePresent(String csvPath) {
		List<Map<String, String>> expected = TestDataLoader.loadCsv(csvPath);
		String body = context.getLastResponseBody();

		for (Map<String, String> row : expected) {
			String fn = TestDataLoader.requireString(row, "firstName");
			String ln = TestDataLoader.requireString(row, "lastName");
			JSONArray matches = JsonPath.read(body,
				"$[?(@.firstName=='" + fn + "' && @.lastName=='" + ln + "')]");
			assertThat(matches).as("Expected vet '%s %s' in response", fn, ln).isNotEmpty();
		}
	}

}
