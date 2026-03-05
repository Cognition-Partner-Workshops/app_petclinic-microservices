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
package org.springframework.samples.petclinic.visits.model;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Visit} domain objects.
 * <p>
 * All query method names follow Spring Data naming conventions, enabling
 * automatic query derivation without explicit JPQL.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Maciej Szarlinski
 * @see <a href="https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html">Spring Data JPA Query Methods</a>
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    /**
     * Retrieves all visits for a specific pet.
     *
     * @param petId the ID of the pet whose visits to retrieve
     * @return a list of visits for the given pet
     */
    List<Visit> findByPetId(int petId);

    /**
     * Retrieves all visits for multiple pets in a single query.
     *
     * @param petIds the collection of pet IDs to look up visits for
     * @return a list of visits belonging to any of the specified pets
     */
    List<Visit> findByPetIdIn(Collection<Integer> petIds);
}
