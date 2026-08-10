package org.springframework.samples.petclinic.api.application;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.api.dto.Visits;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VisitsServiceClientIntegrationTest {

    private static final Integer PET_ID = 1;

    private VisitsServiceClient visitsServiceClient;

    private MockWebServer server;

    @BeforeEach
    void setUp() {
        server = new MockWebServer();
        visitsServiceClient = new VisitsServiceClient(WebClient.builder());
        visitsServiceClient.setHostname(server.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        this.server.close();
    }

    @Test
    void getVisitsForPets_withAvailableVisitsService() {
        prepareResponse();

        Mono<Visits> visits = visitsServiceClient.getVisitsForPets(Collections.singletonList(1));

        assertVisitDescriptionEquals(visits.block(), PET_ID,"test visit");
    }


    @Test
    void getVisitsForPets_withSeveralPetIdsJoinedInQuery() throws InterruptedException {
        prepareResponse();

        visitsServiceClient.getVisitsForPets(List.of(1, 2, 3)).block();

        assertEquals("1,2,3", server.takeRequest().getRequestUrl().queryParameter("petId"));
    }

    @Test
    void getVisitsForPets_withEmptyResponse() {
        server.enqueue(new MockResponse.Builder()
            .addHeader("Content-Type", "application/json")
            .body("{\"items\":[]}")
            .build());

        Visits visits = visitsServiceClient.getVisitsForPets(Collections.singletonList(1)).block();

        assertNotNull(visits);
        assertEquals(0, visits.items().size());
    }

    @Test
    void getVisitsForPets_withUnavailableVisitsService() {
        server.enqueue(new MockResponse.Builder().code(500).build());

        Mono<Visits> visits = visitsServiceClient.getVisitsForPets(Collections.singletonList(1));

        assertThrows(WebClientResponseException.InternalServerError.class, visits::block);
    }

    private void assertVisitDescriptionEquals(Visits visits, int petId, String description) {
        assertEquals(1, visits.items().size());
        assertNotNull(visits.items().get(0));
        assertEquals(petId, visits.items().get(0).petId());
        assertEquals(description, visits.items().get(0).description());
    }

    private void prepareResponse() {
        MockResponse response = new MockResponse.Builder()
            .addHeader("Content-Type", "application/json")
            .body("{\"items\":[{\"id\":5,\"date\":\"2018-11-15\",\"description\":\"test visit\",\"petId\":1}]}")
            .build();
        this.server.enqueue(response);
    }

}
