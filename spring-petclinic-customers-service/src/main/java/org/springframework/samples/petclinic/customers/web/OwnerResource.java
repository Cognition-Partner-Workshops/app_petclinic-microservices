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
package org.springframework.samples.petclinic.customers.web;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller exposing CRUD endpoints for pet owner management.
 * <p>
 * Handles HTTP requests under the {@code /owners} path and delegates persistence
 * operations to {@link OwnerRepository}. All endpoints are instrumented with
 * Micrometer's {@code petclinic.owner} timer for observability.
 *
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RequestMapping("/owners")
@RestController
@Timed("petclinic.owner")
class OwnerResource {

    private static final Logger log = LoggerFactory.getLogger(OwnerResource.class);

    private final OwnerRepository ownerRepository;
    private final OwnerEntityMapper ownerEntityMapper;

    /**
     * Constructs the resource with the required repository and mapper.
     *
     * @param ownerRepository   the JPA repository for {@link Owner} persistence
     * @param ownerEntityMapper the mapper for converting {@link OwnerRequest} to {@link Owner}
     */
    OwnerResource(OwnerRepository ownerRepository, OwnerEntityMapper ownerEntityMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerEntityMapper = ownerEntityMapper;
    }

    /**
     * Creates a new pet owner from the supplied request data.
     *
     * @param ownerRequest the validated owner data to persist
     * @return the newly created {@link Owner} entity with its generated ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Owner createOwner(@Valid @RequestBody OwnerRequest ownerRequest) {
        Owner owner = ownerEntityMapper.map(new Owner(), ownerRequest);
        return ownerRepository.save(owner);
    }

    /**
     * Retrieves a single owner by their unique identifier.
     *
     * @param ownerId the owner's ID (must be >= 1)
     * @return an {@link Optional} containing the owner if found, or empty otherwise
     */
    @GetMapping(value = "/{ownerId}")
    public Optional<Owner> findOwner(@PathVariable("ownerId") @Min(1) int ownerId) {
        return ownerRepository.findById(ownerId);
    }

    /**
     * Retrieves all registered pet owners.
     *
     * @return a list of all {@link Owner} entities
     */
    @GetMapping
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    /**
     * Updates an existing owner's information.
     *
     * @param ownerId      the ID of the owner to update (must be >= 1)
     * @param ownerRequest the validated updated owner data
     * @throws ResourceNotFoundException if no owner exists with the given ID
     */
    @PutMapping(value = "/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwner(@PathVariable("ownerId") @Min(1) int ownerId, @Valid @RequestBody OwnerRequest ownerRequest) {
        final Owner ownerModel = ownerRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));

        ownerEntityMapper.map(ownerModel, ownerRequest);
        log.info("Saving owner {}", ownerModel);
        ownerRepository.save(ownerModel);
    }
}
