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

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Data Transfer Object for creating or updating a pet via the Customers microservice.
 * <p>
 * Used by the GenAI service's tool functions to send pet data to the Customers REST API.
 *
 * @param id        the pet identifier (used during updates; ignored on creation)
 * @param birthDate the pet's date of birth in {@code yyyy-MM-dd} format
 * @param name      the pet's name
 * @param typeId    the ID of the pet type (e.g. 1=cat, 2=dog)
 * @author Oded Shopen
 */
public record PetRequest(int id,
                  @JsonFormat(pattern = "yyyy-MM-dd")
                  Date birthDate,
                  String name,
                  int typeId
) {

}
