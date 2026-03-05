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
package org.springframework.samples.petclinic.genai.dto;

import java.util.Set;

/**
 * Data Transfer Object representing a veterinarian and their specialties,
 * as returned by the Vets microservice.
 * <p>
 * Used by the GenAI service for vector store embeddings and similarity searches.
 *
 * @param id            the unique identifier of the vet
 * @param firstName     the vet's first name
 * @param lastName      the vet's last name
 * @param specialties   the set of specialties this vet holds
 * @author Oded Shopen
 */
public record Vet(
    Integer id,
    String firstName,
    String lastName,
    Set<Specialty> specialties) {
}
