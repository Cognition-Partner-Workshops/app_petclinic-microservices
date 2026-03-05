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
package org.springframework.samples.petclinic.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the Customers microservice.
 * <p>
 * Manages pet owner and pet data for the PetClinic system. Exposes REST endpoints
 * for creating, reading, and updating owners and their associated pets. Persists
 * data using Spring Data JPA and registers with Eureka for service discovery.
 *
 * @author Maciej Szarlinski
 * @see org.springframework.cloud.client.discovery.EnableDiscoveryClient
 */
@EnableDiscoveryClient
@SpringBootApplication
public class CustomersServiceApplication {

	/**
	 * Launches the Customers Service application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(CustomersServiceApplication.class, args);
	}
}
