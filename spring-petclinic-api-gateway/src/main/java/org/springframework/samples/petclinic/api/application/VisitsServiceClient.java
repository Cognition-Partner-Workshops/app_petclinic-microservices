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
package org.springframework.samples.petclinic.api.application;

import org.springframework.samples.petclinic.api.dto.Visits;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Reactive client for communicating with the Visits microservice.
 * <p>
 * Uses a load-balanced {@link WebClient} to resolve the {@code visits-service}
 * hostname through Eureka and retrieve visit records for one or more pets.
 *
 * @author Maciej Szarlinski
 */
@Component
public class VisitsServiceClient {

    /** Base URL of the Visits service. Can be overridden for testing purposes. */
    private String hostname = "http://visits-service/";

    /** Load-balanced WebClient builder injected by Spring, used to construct HTTP requests. */
    private final WebClient.Builder webClientBuilder;

    /**
     * Constructs the client with a load-balanced {@link WebClient.Builder}.
     *
     * @param webClientBuilder the load-balanced WebClient builder for service-to-service calls
     */
    public VisitsServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Retrieves all visits associated with the given pet IDs from the Visits service.
     *
     * @param petIds the list of pet identifiers to look up visits for
     * @return a {@link Mono} emitting a {@link Visits} object containing all matching visit records
     */
    public Mono<Visits> getVisitsForPets(final List<Integer> petIds) {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "pets/visits?petId={petId}", joinIds(petIds))
            .retrieve()
            .bodyToMono(Visits.class);
    }

    /**
     * Joins a list of pet IDs into a comma-separated string for use as a query parameter.
     *
     * @param petIds the list of pet identifiers to join
     * @return a comma-separated string of pet IDs (e.g. {@code "1,2,3"})
     */
    private String joinIds(List<Integer> petIds) {
        return petIds.stream().map(Object::toString).collect(joining(","));
    }

    /**
     * Overrides the default Visits service hostname. Intended for use in integration tests.
     *
     * @param hostname the base URL to use for the Visits service
     */
    void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
