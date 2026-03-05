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

import java.util.List;

/**
 * Data Transfer Object representing a pet and its visit history, as returned by
 * the Customers microservice.
 *
 * @param id        the unique identifier of the pet
 * @param name      the pet's name
 * @param birthDate the pet's date of birth as a {@code yyyy-MM-dd} string
 * @param type      the pet's species classification
 * @param visits    the list of veterinary visits associated with this pet
 * @author Oded Shopen
 */
public record PetDetails(
    int id,
    String name,
    String birthDate,
    PetType type,
    List<VisitDetails> visits
){
}
