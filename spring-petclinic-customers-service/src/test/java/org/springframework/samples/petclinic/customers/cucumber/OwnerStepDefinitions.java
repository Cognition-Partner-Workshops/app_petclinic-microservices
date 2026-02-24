package org.springframework.samples.petclinic.customers.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step definitions for Owner-related Cucumber scenarios.
 */
public class OwnerStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult lastResult;
    private int createdOwnerId;

    // ── Given ──────────────────────────────────────────────────────────

    @Given("the PetClinic system is running")
    public void thePetclinicSystemIsRunning() {
        // Spring Boot context is already started by CucumberSpringConfiguration
    }

    @Given("there are no owners in the system")
    public void thereAreNoOwnersInTheSystem() {
        ownerRepository.deleteAll();
    }

    @Given("the following owner exists:")
    public void theFollowingOwnerExists(io.cucumber.datatable.DataTable dataTable) {
        var row = dataTable.asMaps().get(0);
        Owner owner = new Owner();
        owner.setFirstName(row.get("firstName"));
        owner.setLastName(row.get("lastName"));
        owner.setAddress(row.get("address"));
        owner.setCity(row.get("city"));
        owner.setTelephone(row.get("telephone"));
        Owner saved = ownerRepository.save(owner);
        createdOwnerId = saved.getId();
    }

    // ── When ───────────────────────────────────────────────────────────

    @When("I create an owner with first name {string}, last name {string}, address {string}, city {string}, and telephone {string}")
    public void iCreateAnOwner(String firstName, String lastName, String address, String city, String telephone) throws Exception {
        String json = """
            {
                "firstName": "%s",
                "lastName": "%s",
                "address": "%s",
                "city": "%s",
                "telephone": "%s"
            }
            """.formatted(firstName, lastName, address, city, telephone);

        lastResult = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andReturn();
    }

    @When("I request the owner list")
    public void iRequestTheOwnerList() throws Exception {
        lastResult = mockMvc.perform(get("/owners")
                .accept(MediaType.APPLICATION_JSON))
            .andReturn();
    }

    @When("I request that owner by ID")
    public void iRequestThatOwnerById() throws Exception {
        lastResult = mockMvc.perform(get("/owners/{id}", createdOwnerId)
                .accept(MediaType.APPLICATION_JSON))
            .andReturn();
    }

    @When("I update the owner's city to {string}")
    public void iUpdateTheOwnersCity(String newCity) throws Exception {
        Owner existing = ownerRepository.findById(createdOwnerId).orElseThrow();
        String json = """
            {
                "firstName": "%s",
                "lastName": "%s",
                "address": "%s",
                "city": "%s",
                "telephone": "%s"
            }
            """.formatted(existing.getFirstName(), existing.getLastName(),
            existing.getAddress(), newCity, existing.getTelephone());

        lastResult = mockMvc.perform(put("/owners/{id}", createdOwnerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andReturn();
    }

    // ── Then ───────────────────────────────────────────────────────────

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the response should contain {string}")
    public void theResponseShouldContain(String expected) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).contains(expected);
    }

    @Then("the owner list should have {int} entry")
    public void theOwnerListShouldHaveEntry(int count) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        var list = objectMapper.readTree(body);
        assertThat(list.size()).isEqualTo(count);
    }

    @Then("the owner list should have {int} entries")
    public void theOwnerListShouldHaveEntries(int count) throws Exception {
        theOwnerListShouldHaveEntry(count);
    }

    @Then("the owner's city should be {string}")
    public void theOwnersCityShouldBe(String expectedCity) {
        Owner owner = ownerRepository.findById(createdOwnerId).orElseThrow();
        assertThat(owner.getCity()).isEqualTo(expectedCity);
    }
}
