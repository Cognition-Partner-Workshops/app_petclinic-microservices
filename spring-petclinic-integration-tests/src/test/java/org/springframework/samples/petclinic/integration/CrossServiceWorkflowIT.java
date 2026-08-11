package org.springframework.samples.petclinic.integration;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.samples.petclinic.integration.support.AbstractPetClinicIT;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.samples.petclinic.integration.support.PetClinicFixtures.ownerRequest;
import static org.springframework.samples.petclinic.integration.support.PetClinicFixtures.petRequest;
import static org.springframework.samples.petclinic.integration.support.PetClinicFixtures.visitRequest;

/**
 * Walks a single pet owner through the whole customers-service / visits-service /
 * api-gateway workflow and asserts that the gateway aggregates the data owned by both
 * services.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class CrossServiceWorkflowIT extends AbstractPetClinicIT {

    private static final String PET_NAME = "Fluffy";

    private static final String PET_BIRTH_DATE = "2020-05-17";

    private static final int PET_TYPE_CAT = 1;

    private static final String VISIT_DATE = "2024-03-04";

    private static final String VISIT_DESCRIPTION = "Annual checkup";

    private static int ownerId;

    private static int petId;

    private static int visitId;

    @Test
    @Order(1)
    void registersANewOwnerInTheCustomersService() {
        JsonPath owner = customersService()
            .body(ownerRequest("Composeson"))
            .post("/owners")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath();

        ownerId = owner.getInt("id");
        assertThat(ownerId).isPositive();
        assertThat(owner.getString("lastName")).isEqualTo("Composeson");

        JsonPath persisted = customersService()
            .get("/owners/{ownerId}", ownerId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        assertThat(persisted.getString("city")).isEqualTo("Testville");
        assertThat(persisted.getList("pets")).isEmpty();
    }

    @Test
    @Order(2)
    void addsAPetToTheOwner() {
        JsonPath pet = customersService()
            .body(petRequest(PET_NAME, PET_BIRTH_DATE, PET_TYPE_CAT))
            .post("/owners/{ownerId}/pets", ownerId)
            .then()
            .statusCode(201)
            .extract()
            .jsonPath();

        petId = pet.getInt("id");
        assertThat(petId).isPositive();
        assertThat(pet.getString("name")).isEqualTo(PET_NAME);

        JsonPath persisted = customersService()
            .get("/owners/{ownerId}/pets/{petId}", ownerId, petId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        assertThat(persisted.getString("type.name")).isEqualTo("cat");
        assertThat(persisted.getString("owner")).contains("Composeson");
    }

    @Test
    @Order(3)
    void schedulesAVisitInTheVisitsService() {
        JsonPath visit = visitsService()
            .body(visitRequest(VISIT_DATE, VISIT_DESCRIPTION))
            .post("/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
            .then()
            .statusCode(201)
            .extract()
            .jsonPath();

        visitId = visit.getInt("id");
        assertThat(visitId).isPositive();
        assertThat(visit.getInt("petId")).isEqualTo(petId);

        JsonPath visits = visitsService()
            .get("/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        assertThat(visits.getList("id", Integer.class)).containsExactly(visitId);
        assertThat(visits.getString("[0].description")).isEqualTo(VISIT_DESCRIPTION);
    }

    @Test
    @Order(4)
    void gatewayAggregatesOwnerPetsAndVisits() {
        JsonPath aggregated = gateway()
            .get("/api/gateway/owners/{ownerId}", ownerId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();

        assertThat(aggregated.getInt("id")).isEqualTo(ownerId);
        assertThat(aggregated.getString("lastName")).isEqualTo("Composeson");
        assertThat(aggregated.getList("pets")).hasSize(1);
        assertThat(aggregated.getString("pets[0].name")).isEqualTo(PET_NAME);
        assertThat(aggregated.getString("pets[0].type.name")).isEqualTo("cat");
        assertThat(aggregated.getList("pets[0].visits")).hasSize(1);
        assertThat(aggregated.getInt("pets[0].visits[0].id")).isEqualTo(visitId);
        assertThat(aggregated.getInt("pets[0].visits[0].petId")).isEqualTo(petId);
        assertThat(aggregated.getString("pets[0].visits[0].description")).isEqualTo(VISIT_DESCRIPTION);
    }

    @Test
    @Order(5)
    void gatewayRoutesToTheCustomersService() {
        JsonPath owner = gateway()
            .get("/api/customer/owners/{ownerId}", ownerId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();

        assertThat(owner.getInt("id")).isEqualTo(ownerId);
        assertThat(owner.getList("pets")).hasSize(1);
        // The customers-service knows nothing about visits
        Object visitsOfPet = owner.get("pets[0].visits");
        assertThat(visitsOfPet).isNull();

        List<String> petTypes = gateway()
            .get("/api/customer/petTypes")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("name", String.class);
        assertThat(petTypes).contains("cat", "dog");
    }

    @Test
    @Order(6)
    void gatewayRoutesToTheVisitsService() {
        JsonPath visits = gateway()
            .get("/api/visit/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        assertThat(visits.getList("id", Integer.class)).containsExactly(visitId);

        JsonPath byPetIds = gateway()
            .get("/api/visit/pets/visits?petId={petId}", petId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
        assertThat(byPetIds.getList("items.petId", Integer.class)).containsExactly(petId);
    }

    @Test
    @Order(7)
    void gatewayRejectsUnknownOwners() {
        gateway()
            .get("/api/customer/owners/{ownerId}", 9999)
            .then()
            .statusCode(200)
            .body(org.hamcrest.Matchers.emptyOrNullString());
    }
}
