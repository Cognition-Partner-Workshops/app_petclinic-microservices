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

/**
 * Data Transfer Object representing a veterinary visit record.
 * <p>
 * Used by the GenAI service to deserialize visit data from the Visits microservice
 * when providing AI-assisted answers about pet visit history.
 *
 * @param id          the unique identifier of the visit
 * @param petId       the ID of the pet this visit belongs to
 * @param date        the date of the visit as a string
 * @param description a free-text description of the visit
 * @author Oded Shopen
 */
public record VisitDetails(
    Integer id,
    Integer petId,
    String date,
    String description) {
}
