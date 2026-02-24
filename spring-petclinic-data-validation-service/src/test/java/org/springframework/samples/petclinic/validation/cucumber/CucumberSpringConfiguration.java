package org.springframework.samples.petclinic.validation.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring context configuration for Cucumber tests in the data-validation-service.
 * Boots the full application context with an in-memory database and auto-configured MockMvc.
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackages = "org.springframework.samples.petclinic.test")
public class CucumberSpringConfiguration {

}
