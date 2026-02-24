package org.springframework.samples.petclinic.visits.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring context configuration for Cucumber tests in the visits-service.
 * Boots the full application context with an in-memory database and auto-configured MockMvc.
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

}
