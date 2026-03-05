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
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints for pet management within the Customers service.
 * <p>
 * Provides CRUD operations for pets, including listing available pet types,
 * creating new pets under an owner, updating existing pets, and retrieving
 * pet details. All endpoints are instrumented with Micrometer's
 * {@code petclinic.pet} timer for observability.
 *
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Szarlinski
 * @author Ramazan Sakin
 */
@RestController
@Timed("petclinic.pet")
class PetResource {

    private static final Logger log = LoggerFactory.getLogger(PetResource.class);

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    /**
     * Constructs the resource with the required repositories.
     *
     * @param petRepository   the JPA repository for {@link Pet} persistence
     * @param ownerRepository the JPA repository for {@link Owner} lookups
     */
    PetResource(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    /**
     * Lists all available pet types, sorted alphabetically by name.
     *
     * @return a list of all {@link PetType} entries
     */
    @GetMapping("/petTypes")
    public List<PetType> getPetTypes() {
        return petRepository.findPetTypes();
    }

    /**
     * Creates a new pet and associates it with the specified owner.
     *
     * @param petRequest the pet data to create
     * @param ownerId    the ID of the owner to add the pet to (must be >= 1)
     * @return the newly persisted {@link Pet} entity
     * @throws ResourceNotFoundException if no owner exists with the given ID
     */
    @PostMapping("/owners/{ownerId}/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public Pet processCreationForm(
        @RequestBody PetRequest petRequest,
        @PathVariable("ownerId") @Min(1) int ownerId) {

        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));

        final Pet pet = new Pet();
        owner.addPet(pet);
        return save(pet, petRequest);
    }

    /**
     * Updates an existing pet's details (name, birth date, type).
     *
     * @param petRequest the updated pet data (the pet ID is extracted from the request body)
     * @throws ResourceNotFoundException if no pet exists with the given ID
     */
    @PutMapping("/owners/*/pets/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void processUpdateForm(@RequestBody PetRequest petRequest) {
        int petId = petRequest.id();
        Pet pet = findPetById(petId);
        save(pet, petRequest);
    }

    /**
     * Applies the request data to a {@link Pet} entity and persists it.
     * Looks up the pet type by ID and sets it if found.
     *
     * @param pet        the pet entity to update
     * @param petRequest the data to apply
     * @return the saved {@link Pet} entity
     */
    private Pet save(final Pet pet, final PetRequest petRequest) {

        pet.setName(petRequest.name());
        pet.setBirthDate(petRequest.birthDate());

        petRepository.findPetTypeById(petRequest.typeId())
            .ifPresent(pet::setType);

        log.info("Saving pet {}", pet);
        return petRepository.save(pet);
    }

    /**
     * Retrieves the details of a single pet by its identifier.
     *
     * @param petId the unique identifier of the pet
     * @return a {@link PetDetails} DTO containing the pet's data
     * @throws ResourceNotFoundException if no pet exists with the given ID
     */
    @GetMapping("owners/*/pets/{petId}")
    public PetDetails findPet(@PathVariable("petId") int petId) {
        Pet pet = findPetById(petId);
        return new PetDetails(pet);
    }


    /**
     * Looks up a pet by ID or throws a {@link ResourceNotFoundException}.
     *
     * @param petId the pet identifier to look up
     * @return the found {@link Pet} entity
     * @throws ResourceNotFoundException if no pet exists with the given ID
     */
    private Pet findPetById(int petId) {
        return petRepository.findById(petId)
            .orElseThrow(() -> new ResourceNotFoundException("Pet " + petId + " not found"));
    }

}
