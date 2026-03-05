/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.api.boundary.web;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.samples.petclinic.api.dto.Visits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * REST controller that aggregates data from the Customers and Visits microservices.
 * <p>
 * Serves as the backend-for-frontend (BFF) layer, composing owner details with their
 * pets' visit histories in a single response. Uses reactive programming ({@link Mono})
 * for non-blocking I/O and a Resilience4j circuit breaker to gracefully degrade when
 * the Visits service is unavailable.
 *
 * @author Maciej Szarlinski
 */
@RestController
@RequestMapping("/api/gateway")
public class ApiGatewayController {

    private final CustomersServiceClient customersServiceClient;

    private final VisitsServiceClient visitsServiceClient;

    private final ReactiveCircuitBreakerFactory cbFactory;

    /**
     * Constructs the controller with the required service clients and circuit breaker factory.
     *
     * @param customersServiceClient client for retrieving owner and pet data
     * @param visitsServiceClient    client for retrieving visit records
     * @param cbFactory              factory for creating reactive circuit breakers
     */
    public ApiGatewayController(CustomersServiceClient customersServiceClient,
                                VisitsServiceClient visitsServiceClient,
                                ReactiveCircuitBreakerFactory cbFactory) {
        this.customersServiceClient = customersServiceClient;
        this.visitsServiceClient = visitsServiceClient;
        this.cbFactory = cbFactory;
    }

    /**
     * Retrieves complete owner details including pet information and visit history.
     * <p>
     * Fetches the owner from the Customers service, then enriches each pet with its
     * visit records from the Visits service. The visits call is wrapped in a circuit
     * breaker that returns an empty visits list if the Visits service is unavailable.
     *
     * @param ownerId the unique identifier of the owner
     * @return a {@link Mono} emitting the fully composed {@link OwnerDetails}
     */
    @GetMapping(value = "owners/{ownerId}")
    public Mono<OwnerDetails> getOwnerDetails(final @PathVariable int ownerId) {
        return customersServiceClient.getOwner(ownerId)
            .flatMap(owner ->
                visitsServiceClient.getVisitsForPets(owner.getPetIds())
                    .transform(it -> {
                        ReactiveCircuitBreaker cb = cbFactory.create("getOwnerDetails");
                        return cb.run(it, throwable -> emptyVisitsForPets());
                    })
                    .map(addVisitsToOwner(owner))
            );

    }

    /**
     * Returns a mapping function that associates each visit with the correct pet
     * belonging to the given owner.
     *
     * @param owner the owner whose pets should be enriched with visit data
     * @return a function that maps {@link Visits} onto the owner's pets and returns the owner
     */
    private Function<Visits, OwnerDetails> addVisitsToOwner(OwnerDetails owner) {
        return visits -> {
            owner.pets()
                .forEach(pet -> pet.visits()
                    .addAll(visits.items().stream()
                        .filter(v -> v.petId() == pet.id())
                        .toList())
                );
            return owner;
        };
    }

    /**
     * Provides a fallback that returns an empty {@link Visits} collection.
     * Used by the circuit breaker when the Visits service is unreachable.
     *
     * @return a {@link Mono} emitting an empty {@link Visits} instance
     */
    private Mono<Visits> emptyVisitsForPets() {
        return Mono.just(new Visits(List.of()));
    }
}
