package org.springframework.samples.petclinic.jhipster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * JHipster backend microservice extracted from the JHipster monolith.
 * Provides authentication, account management, and entity CRUD endpoints
 * for BankAccount, Label, and Operation.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class JhipsterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JhipsterServiceApplication.class, args);
	}
}
