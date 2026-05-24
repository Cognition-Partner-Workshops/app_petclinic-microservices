package org.springframework.samples.petclinic.api.boundary.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.samples.petclinic.api.dto.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiGatewayControllerTest {

    @Mock
    CustomersServiceClient customersServiceClient;

    @Mock
    VisitsServiceClient visitsServiceClient;

    @Mock
    ReactiveCircuitBreakerFactory cbFactory;

    @Mock
    ReactiveCircuitBreaker circuitBreaker;

    @InjectMocks
    ApiGatewayController controller;

    @Test
    void getOwnerDetailsReturnsOwnerWithVisits() {
        PetDetails pet = new PetDetails(10, "Garfield", "2020-01-01",
            new PetType("cat"), new ArrayList<>());
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1).firstName("George").lastName("Franklin")
            .pets(List.of(pet))
            .build();

        VisitDetails matchingVisit = new VisitDetails(1, 10, "2024-01-01", "checkup");
        VisitDetails nonMatchingVisit = new VisitDetails(2, 999, "2024-02-01", "surgery");
        Visits visits = new Visits(List.of(matchingVisit, nonMatchingVisit));

        when(customersServiceClient.getOwner(1)).thenReturn(Mono.just(owner));
        when(visitsServiceClient.getVisitsForPets(any())).thenReturn(Mono.just(visits));
        when(cbFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(controller.getOwnerDetails(1))
            .assertNext(result -> {
                assertThat(result.firstName()).isEqualTo("George");
                assertThat(result.pets().get(0).visits()).hasSize(1);
            })
            .verifyComplete();
    }

    @Test
    void getOwnerDetailsFallsBackToEmptyVisitsOnError() {
        PetDetails pet = new PetDetails(10, "Garfield", "2020-01-01",
            new PetType("cat"), new ArrayList<>());
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1).firstName("George").lastName("Franklin")
            .pets(List.of(pet))
            .build();

        when(customersServiceClient.getOwner(1)).thenReturn(Mono.just(owner));
        when(visitsServiceClient.getVisitsForPets(any())).thenReturn(Mono.error(new RuntimeException("Service down")));
        when(cbFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(invocation -> {
            java.util.function.Function<Throwable, Mono<Visits>> fallback = invocation.getArgument(1);
            return fallback.apply(new RuntimeException("Service down"));
        });

        StepVerifier.create(controller.getOwnerDetails(1))
            .assertNext(result -> {
                assertThat(result.firstName()).isEqualTo("George");
                assertThat(result.pets().get(0).visits()).isEmpty();
            })
            .verifyComplete();
    }
}
