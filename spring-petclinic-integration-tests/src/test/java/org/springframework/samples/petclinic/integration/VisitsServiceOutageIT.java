package org.springframework.samples.petclinic.integration;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.samples.petclinic.integration.support.AbstractPetClinicIT;
import org.springframework.samples.petclinic.integration.support.PetClinicEnvironment;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.samples.petclinic.integration.support.PetClinicFixtures.ownerRequest;
import static org.springframework.samples.petclinic.integration.support.PetClinicFixtures.petRequest;
import static org.springframework.samples.petclinic.integration.support.PetClinicFixtures.visitRequest;

/**
 * Verifies how the API gateway degrades while the visits-service is down: the aggregation
 * endpoint keeps serving customer data through the {@code getOwnerDetails} circuit breaker
 * fallback, while the plain proxy route to the visits-service stops serving data. Once the
 * service is back, visits are aggregated again.
 * <p>
 * The visits-service uses an in-memory database, so its data does not survive a restart
 * and the visit is recreated after the service comes back up.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class VisitsServiceOutageIT extends AbstractPetClinicIT {

    private static final String VISIT_DESCRIPTION = "Vaccination";

    private static int ownerId;

    private static int petId;

    @BeforeAll
    static void createOwnerWithVisit() {
        ownerId = customersService()
            .body(ownerRequest("Fallbackson"))
            .post("/owners")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getInt("id");

        petId = customersService()
            .body(petRequest("Basil", "2019-02-11", 2))
            .post("/owners/{ownerId}/pets", ownerId)
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getInt("id");

        scheduleVisit();
    }

    @AfterAll
    static void ensureVisitsServiceIsRunning() {
        environment.startService(PetClinicEnvironment.VISITS_SERVICE);
    }

    @Test
    @Order(1)
    void aggregatesVisitsWhileTheVisitsServiceIsUp() {
        JsonPath aggregated = ownerDetails();

        assertThat(aggregated.getString("pets[0].name")).isEqualTo("Basil");
        assertThat(aggregated.getList("pets[0].visits")).hasSize(1);
        assertThat(aggregated.getString("pets[0].visits[0].description")).isEqualTo(VISIT_DESCRIPTION);
    }

    @Test
    @Order(2)
    void gatewayServesOwnerWithoutVisitsWhileVisitsServiceIsDown() {
        environment.stopService(PetClinicEnvironment.VISITS_SERVICE);

        // The gateway keeps its Eureka cache for a while, so the first calls may still be
        // routed to the dead instance before the fallback kicks in.
        await("owner details to be served from the circuit breaker fallback")
            .atMost(Duration.ofMinutes(2))
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                JsonPath aggregated = ownerDetails();
                assertThat(aggregated.getInt("id")).isEqualTo(ownerId);
                assertThat(aggregated.getString("pets[0].name")).isEqualTo("Basil");
                assertThat(aggregated.getList("pets[0].visits")).isEmpty();
            });
    }

    @Test
    @Order(3)
    void visitsRouteStopsServingDataWhileVisitsServiceIsDown() {
        // The default CircuitBreaker filter forwards to /fallback, which is only mapped for
        // POST, so the proxied GET ends up as 405 instead of a 5xx of its own
        await("the visits route to stop serving data")
            .atMost(Duration.ofMinutes(2))
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> assertThat(gateway()
                .get("/api/visit/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
                .statusCode()).isIn(405, 500, 503, 504));

        gateway().get("/api/customer/owners/{ownerId}", ownerId).then().statusCode(200);
    }

    @Test
    @Order(4)
    void aggregatesVisitsAgainOnceTheVisitsServiceIsBack() {
        environment.startService(PetClinicEnvironment.VISITS_SERVICE);

        await("the visits route to serve data again")
            .atMost(Duration.ofMinutes(3))
            .pollInterval(3, TimeUnit.SECONDS)
            .ignoreExceptions()
            .until(() -> gateway()
                .get("/api/visit/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
                .statusCode() == 200);

        // The in-memory database of the restarted service lost the visit
        scheduleVisit();

        await("visits to be aggregated again")
            .atMost(Duration.ofMinutes(2))
            .pollInterval(2, TimeUnit.SECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                List<Object> visits = ownerDetails().getList("pets[0].visits");
                assertThat(visits).hasSize(1);
            });
    }

    private static void scheduleVisit() {
        visitsService()
            .body(visitRequest("2024-06-01", VISIT_DESCRIPTION))
            .post("/owners/{ownerId}/pets/{petId}/visits", ownerId, petId)
            .then()
            .statusCode(201);
    }

    private static JsonPath ownerDetails() {
        return gateway()
            .get("/api/gateway/owners/{ownerId}", ownerId)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();
    }
}
