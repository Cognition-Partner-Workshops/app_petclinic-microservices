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
package org.springframework.samples.petclinic.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Data Transfer Object representing the full details of a pet owner,
 * including their personal information and a list of their pets.
 * <p>
 * Used by the API Gateway to compose responses that aggregate data
 * from the Customers and Visits microservices.
 *
 * @param id        the unique identifier of the owner
 * @param firstName the owner's first name
 * @param lastName  the owner's last name
 * @param address   the owner's street address
 * @param city      the owner's city of residence
 * @param telephone the owner's telephone number
 * @param pets      the list of pets belonging to this owner
 * @author Maciej Szarlinski
 */
public record OwnerDetails(
    int id,
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone,
    List<PetDetails> pets) {

    /**
     * Extracts the list of pet IDs from this owner's pets.
     * Excluded from JSON serialization since it is a derived convenience accessor.
     *
     * @return an unmodifiable list of pet identifiers
     */
    @JsonIgnore
    public List<Integer> getPetIds() {
        return pets.stream()
            .map(PetDetails::id)
            .toList();
    }


    /**
     * Builder for constructing {@link OwnerDetails} instances, primarily used in tests.
     */
    public static final class OwnerDetailsBuilder {
        private int id;
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private String telephone;
        private List<PetDetails> pets;

        private OwnerDetailsBuilder() {
        }

        /**
         * Creates a new builder instance.
         *
         * @return a new {@link OwnerDetailsBuilder}
         */
        public static OwnerDetailsBuilder anOwnerDetails() {
            return new OwnerDetailsBuilder();
        }

        public OwnerDetailsBuilder id(int id) {
            this.id = id;
            return this;
        }

        public OwnerDetailsBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public OwnerDetailsBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public OwnerDetailsBuilder address(String address) {
            this.address = address;
            return this;
        }

        public OwnerDetailsBuilder city(String city) {
            this.city = city;
            return this;
        }

        public OwnerDetailsBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public OwnerDetailsBuilder pets(List<PetDetails> pets) {
            this.pets = pets;
            return this;
        }

        /**
         * Builds a new {@link OwnerDetails} record from the accumulated builder state.
         *
         * @return a fully constructed {@link OwnerDetails} instance
         */
        public OwnerDetails build() {
            return new OwnerDetails(id, firstName, lastName, address, city, telephone, pets);
        }
    }
}
