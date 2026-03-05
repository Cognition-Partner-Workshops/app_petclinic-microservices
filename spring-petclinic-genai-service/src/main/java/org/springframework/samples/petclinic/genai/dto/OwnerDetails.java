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
 * Data Transfer Object representing a pet owner as returned by the Customers microservice.
 * <p>
 * Used by the GenAI service to deserialize owner data retrieved via REST calls for
 * AI-powered queries and tool invocations.
 *
 * @param id        the unique identifier of the owner
 * @param firstName the owner's first name
 * @param lastName  the owner's last name
 * @param address   the owner's street address
 * @param city      the owner's city
 * @param telephone the owner's telephone number
 * @param pets      the list of pets belonging to this owner
 * @author Oded Shopen
 */
public record OwnerDetails(
    int id,
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone,
    List<PetDetails> pets
) {
}
