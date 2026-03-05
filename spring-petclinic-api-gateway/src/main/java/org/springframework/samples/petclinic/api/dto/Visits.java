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

import java.util.ArrayList;
import java.util.List;


/**
 * Wrapper Data Transfer Object holding a collection of {@link VisitDetails}.
 * <p>
 * Used to deserialize the response from the Visits microservice. Provides
 * a no-arg constructor that initializes an empty mutable list, which is
 * required for JSON deserialization and circuit breaker fallback scenarios.
 *
 * @param items the list of visit detail records
 * @author Maciej Szarlinski
 */
public record Visits (
    List<VisitDetails> items
) {
    /**
     * No-arg constructor that creates an empty {@link Visits} instance.
     * Required for default deserialization and used as a fallback value.
     */
    public Visits() {
        this(new ArrayList<>());
    }
}
