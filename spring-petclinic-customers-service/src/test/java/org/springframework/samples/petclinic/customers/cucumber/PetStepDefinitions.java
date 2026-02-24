package org.springframework.samples.petclinic.customers.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step definitions for Pet-related Cucumber scenarios.
 */
public class PetStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult lastResult;
    private int ownerId;

    // ── Given ──────────────────────────────────────────────────────────

    @Given("an owner {string} {string} exists in the system")
    public void anOwnerExists(String firstName, String lastName) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress("123 Test St");
        owner.setCity("TestCity");
        owner.setTelephone("1234567890");
        Owner saved = ownerRepository.save(owner);
        ownerId = saved.getId();
    }

    @Given("pet types are available in the database")
    public void petTypesAreAvailable() {
        // The HSQLDB data.sql script pre-loads pet types (cat, dog, lizard, snake, bird, hamster)
    }

    // ── When ───────────────────────────────────────────────────────────

    @When("I add a pet named {string} with birth date {string} and type ID {int} to that owner")
    public void iAddAPet(String name, String birthDate, int typeId) throws Exception {
        String json = """
            {
                "id": 0,
                "name": "%s",
                "birthDate": "%s",
                "typeId": %d
            }
            """.formatted(name, birthDate, typeId);

        lastResult = mockMvc.perform(post("/owners/{ownerId}/pets", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andReturn();
    }

    @When("I request the list of pet types")
    public void iRequestPetTypes() throws Exception {
        lastResult = mockMvc.perform(get("/petTypes")
                .accept(MediaType.APPLICATION_JSON))
            .andReturn();
    }

    // ── Then ───────────────────────────────────────────────────────────

    @Then("the pet response status should be {int}")
    public void thePetResponseStatusShouldBe(int expectedStatus) {
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the pet response should contain {string}")
    public void thePetResponseShouldContain(String expected) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).contains(expected);
    }

    @Then("the pet types list should not be empty")
    public void thePetTypesListShouldNotBeEmpty() throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        var list = objectMapper.readTree(body);
        assertThat(list.size()).isGreaterThan(0);
    }

    @Then("the pet types list should contain {string}")
    public void thePetTypesListShouldContain(String expectedType) throws Exception {
        String body = lastResult.getResponse().getContentAsString();
        assertThat(body).contains(expectedType);
    }
}
