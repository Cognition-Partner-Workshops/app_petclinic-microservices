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

import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Reactive client for communicating with the Customers microservice.
 * <p>
 * Uses a load-balanced {@link WebClient} to resolve the {@code customers-service}
 * hostname through Eureka and retrieve owner details by ID.
 *
 * @author Maciej Szarlinski
 */
@Component
public class CustomersServiceClient {

    /** Load-balanced WebClient builder injected by Spring, used to construct HTTP requests. */
    private final WebClient.Builder webClientBuilder;

    /**
     * Constructs the client with a load-balanced {@link WebClient.Builder}.
     *
     * @param webClientBuilder the load-balanced WebClient builder for service-to-service calls
     */
    public CustomersServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Retrieves the details of a pet owner from the Customers service.
     *
     * @param ownerId the unique identifier of the owner to retrieve
     * @return a {@link Mono} emitting the {@link OwnerDetails} for the specified owner
     */
    public Mono<OwnerDetails> getOwner(final int ownerId) {
        return webClientBuilder.build().get()
            .uri("http://customers-service/owners/{ownerId}", ownerId)
            .retrieve()
            .bodyToMono(OwnerDetails.class);
    }
}
