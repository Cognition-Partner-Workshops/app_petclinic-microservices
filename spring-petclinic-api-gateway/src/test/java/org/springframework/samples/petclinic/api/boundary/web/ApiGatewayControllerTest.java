package org.springframework.samples.petclinic.api.boundary.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.samples.petclinic.api.dto.PetDetails;
import org.springframework.samples.petclinic.api.dto.VisitDetails;
import org.springframework.samples.petclinic.api.dto.Visits;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebFluxTest(controllers = ApiGatewayController.class)
@Import({ReactiveResilience4JAutoConfiguration.class, CircuitBreakerConfiguration.class})
class ApiGatewayControllerTest {

    @MockitoBean
    private CustomersServiceClient customersServiceClient;

    @MockitoBean
    private VisitsServiceClient visitsServiceClient;

    @Autowired
    private WebTestClient client;


    @Test
    void getOwnerDetails_withAvailableVisitsService() {
        PetDetails cat = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(20)
            .name("Garfield")
            .visits(new ArrayList<>())
            .build();
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of(cat))
            .build();
        Mockito
            .when(customersServiceClient.getOwner(1))
            .thenReturn(Mono.just(owner));

        VisitDetails visit = new VisitDetails(300, cat.id(), null, "First visit");
        Visits visits = new Visits(List.of(visit));
        Mockito
            .when(visitsServiceClient.getVisitsForPets(Collections.singletonList(cat.id())))
            .thenReturn(Mono.just(visits));

        client.get()
            .uri("/api/gateway/owners/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.pets[0].name").isEqualTo("Garfield")
            .jsonPath("$.pets[0].visits[0].description").isEqualTo("First visit");
    }

    /**
     * Test Resilience4j fallback method
     */
    @Test
    void getOwnerDetails_withServiceError() {
        PetDetails cat = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(20)
            .name("Garfield")
            .visits(new ArrayList<>())
            .build();
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of(cat))
            .build();
        Mockito
            .when(customersServiceClient.getOwner(1))
            .thenReturn(Mono.just(owner));

        Mockito
            .when(visitsServiceClient.getVisitsForPets(Collections.singletonList(cat.id())))
            .thenReturn(Mono.error(new ConnectException("Simulate error")));

        client.get()
            .uri("/api/gateway/owners/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.pets[0].name").isEqualTo("Garfield")
            .jsonPath("$.pets[0].visits").isEmpty();
    }

    /**
     * The gateway has no error mapping, so a not-found from the customers service surfaces as a server error.
     */
    @Test
    void getOwnerDetails_withUnknownOwner() {
        Mockito
            .when(customersServiceClient.getOwner(999))
            .thenReturn(Mono.error(WebClientResponseException.create(
                404, "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        client.get()
            .uri("/api/gateway/owners/999")
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getOwnerDetails_withNonNumericOwnerId() {
        client.get()
            .uri("/api/gateway/owners/not-a-number")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getOwnerDetails_withoutPets() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1)
            .lastName("Franklin")
            .pets(List.of())
            .build();
        Mockito
            .when(customersServiceClient.getOwner(1))
            .thenReturn(Mono.just(owner));
        Mockito
            .when(visitsServiceClient.getVisitsForPets(List.of()))
            .thenReturn(Mono.just(new Visits(List.of())));

        client.get()
            .uri("/api/gateway/owners/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.lastName").isEqualTo("Franklin")
            .jsonPath("$.pets").isEmpty();
    }

    @Test
    void getOwnerDetails_ignoresVisitsOfOtherPets() {
        PetDetails cat = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(20)
            .name("Garfield")
            .visits(new ArrayList<>())
            .build();
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of(cat))
            .build();
        Mockito
            .when(customersServiceClient.getOwner(1))
            .thenReturn(Mono.just(owner));
        Mockito
            .when(visitsServiceClient.getVisitsForPets(Collections.singletonList(cat.id())))
            .thenReturn(Mono.just(new Visits(List.of(new VisitDetails(300, 999, null, "Other pet")))));

        client.get()
            .uri("/api/gateway/owners/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.pets[0].visits").isEmpty();
    }

}
