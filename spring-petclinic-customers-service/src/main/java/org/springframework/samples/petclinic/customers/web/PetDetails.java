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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

import java.util.Date;

/**
 * Read-only Data Transfer Object representing pet details for API responses.
 * <p>
 * Flattens the owner reference into a display name string and includes the
 * pet's type information. Supports construction from a {@link Pet} entity.
 *
 * @param id        the unique identifier of the pet
 * @param name      the pet's name
 * @param owner     the full name of the pet's owner (first + last)
 * @param birthDate the pet's date of birth
 * @param type      the pet's {@link PetType} classification
 * @author Maciej Szarlinski
 */
record PetDetails(

    long id,

    String name,

    String owner,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date birthDate,

    PetType type
) {
    /**
     * Convenience constructor that maps a {@link Pet} JPA entity to this DTO.
     *
     * @param pet the pet entity to convert
     */
    public PetDetails(Pet pet) {
        this(pet.getId(), pet.getName(), pet.getOwner().getFirstName() + " " + pet.getOwner().getLastName(), pet.getBirthDate(), pet.getType());
    }
}
