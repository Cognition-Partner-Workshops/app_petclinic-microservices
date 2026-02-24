package org.springframework.samples.petclinic.customers.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Bridges Cucumber with the Spring Boot test context.
 *
 * <p>The {@link CucumberContextConfiguration} annotation tells cucumber-spring
 * to use this class as the configuration entry-point. Spring Boot will start the
 * full application context with an in-memory HSQLDB database and mock MVC
 * support so that step definitions can issue HTTP requests without starting a
 * real server.</p>
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // Cucumber picks up this configuration automatically via classpath scanning.
}
