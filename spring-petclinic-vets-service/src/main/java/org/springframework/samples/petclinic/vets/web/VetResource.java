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
package org.springframework.samples.petclinic.vets.web;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing a read-only endpoint for retrieving veterinarian data.
 * <p>
 * Serves the list of all veterinarians and their specialties at {@code GET /vets}.
 * Results are cached under the {@code "vets"} cache region to reduce database load
 * (caching is only active in the {@code production} profile).
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Szarlinski
 */
@RequestMapping("/vets")
@RestController
class VetResource {

    private final VetRepository vetRepository;

    /**
     * Constructs the resource with the required repository.
     *
     * @param vetRepository the JPA repository for {@link Vet} persistence
     */
    VetResource(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    /**
     * Returns all veterinarians with their specialties.
     * Results are cached to avoid repeated database queries.
     *
     * @return a list of all {@link Vet} entities
     */
    @GetMapping
    @Cacheable("vets")
    public List<Vet> showResourcesVetList() {
        return vetRepository.findAll();
    }
}
