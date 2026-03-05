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
package org.springframework.samples.petclinic.visits.web;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints for veterinary visit management.
 * <p>
 * Provides creation and retrieval of visit records for individual pets and
 * batch retrieval across multiple pets. All endpoints are instrumented with
 * Micrometer's {@code petclinic.visit} timer for observability.
 *
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 * @author Ramazan Sakin
 */
@RestController
@Timed("petclinic.visit")
class VisitResource {

    private static final Logger log = LoggerFactory.getLogger(VisitResource.class);

    private final VisitRepository visitRepository;

    /**
     * Constructs the resource with the required repository.
     *
     * @param visitRepository the JPA repository for {@link Visit} persistence
     */
    VisitResource(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    /**
     * Creates a new visit for the specified pet.
     *
     * @param visit the visit data to persist (validated)
     * @param petId the ID of the pet to associate with the visit (must be >= 1)
     * @return the persisted {@link Visit} entity with its generated ID
     */
    @PostMapping("owners/*/pets/{petId}/visits")
    @ResponseStatus(HttpStatus.CREATED)
    public Visit create(
        @Valid @RequestBody Visit visit,
        @PathVariable("petId") @Min(1) int petId) {

        visit.setPetId(petId);
        log.info("Saving visit {}", visit);
        return visitRepository.save(visit);
    }

    /**
     * Retrieves all visits for a specific pet.
     *
     * @param petId the ID of the pet whose visits to retrieve (must be >= 1)
     * @return a list of visits for the given pet
     */
    @GetMapping("owners/*/pets/{petId}/visits")
    public List<Visit> read(@PathVariable("petId") @Min(1) int petId) {
        return visitRepository.findByPetId(petId);
    }

    /**
     * Retrieves visits for multiple pets in a single batch request.
     * <p>
     * Called by the API Gateway to aggregate visit data across several pets.
     *
     * @param petIds the list of pet IDs to look up visits for
     * @return a {@link Visits} wrapper containing the matching visits
     */
    @GetMapping("pets/visits")
    public Visits read(@RequestParam("petId") List<Integer> petIds) {
        final List<Visit> byPetIdIn = visitRepository.findByPetIdIn(petIds);
        return new Visits(byPetIdIn);
    }

    /**
     * Wrapper record for a collection of visits, used as the response body
     * for the batch visit retrieval endpoint.
     *
     * @param items the list of visit records
     */
    record Visits(
        List<Visit> items
    ) {
    }
}
