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
package org.springframework.samples.petclinic.vets.system;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for the Vets microservice, bound from
 * the {@code vets.*} namespace in application configuration.
 *
 * @param cache nested cache configuration settings
 * @author Maciej Szarlinski
 */
@ConfigurationProperties(prefix = "vets")
public record VetsProperties(
    Cache cache
) {
    /**
     * Cache configuration controlling the time-to-live and maximum heap-based
     * entry count for the vets cache.
     *
     * @param ttl      the cache entry time-to-live in seconds
     * @param heapSize the maximum number of entries to keep on the JVM heap
     */
    public record Cache(
        int ttl,
        int heapSize
    ) {
    }
}
