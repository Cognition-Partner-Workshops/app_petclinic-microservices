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
package org.springframework.samples.petclinic.genai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the GenAI microservice.
 * <p>
 * Provides AI-powered chat functionality for the PetClinic system using Spring AI.
 * Integrates with an LLM provider to answer questions about owners, pets, vets,
 * and visits, and supports tool-calling to perform actions (e.g. adding pets or owners).
 * Uses Retrieval-Augmented Generation (RAG) with a vector store of veterinarian data
 * to improve answer quality. Registers with Eureka for service discovery.
 *
 * @author Oded Shopen
 * @see org.springframework.cloud.client.discovery.EnableDiscoveryClient
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GenAIServiceApplication {

	/**
	 * Launches the GenAI Service application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(GenAIServiceApplication.class, args);
	}
}
