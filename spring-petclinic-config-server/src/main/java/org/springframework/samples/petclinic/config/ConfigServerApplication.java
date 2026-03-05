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
package org.springframework.samples.petclinic.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Entry point for the Spring Cloud Config Server application.
 * <p>
 * Provides externalized configuration management for all microservices in the
 * PetClinic ecosystem. Each service retrieves its configuration properties from
 * this centralized server on startup, enabling consistent configuration across
 * environments without redeploying individual services.
 *
 * @author Maciej Szarlinski
 * @see org.springframework.cloud.config.server.EnableConfigServer
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	/**
	 * Launches the Config Server application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
