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

/**
 * Data Transfer Object representing the details of a single veterinary visit.
 *
 * @param id          the unique identifier of the visit
 * @param petId       the identifier of the pet this visit is associated with
 * @param date        the date of the visit (ISO format string)
 * @param description a textual description of the visit (e.g. diagnosis, treatment notes)
 * @author Maciej Szarlinski
 */
public record VisitDetails (
    Integer id,
    Integer petId,
    String date,
    String description) {
}
