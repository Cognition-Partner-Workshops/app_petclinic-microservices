package org.springframework.samples.petclinic.customers.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME;

/**
 * Cucumber test runner configured for the PetClinic Customers Service.
 *
 * <p>This runner is executed by the Maven Failsafe plugin during the
 * {@code integration-test} phase. It discovers and runs all {@code .feature}
 * files located under {@code src/test/resources/features/} and binds them
 * to step definitions in the {@code cucumber} glue package.</p>
 *
 * <h3>Report outputs (CI-friendly):</h3>
 * <ul>
 *   <li><b>HTML</b> &ndash; {@code target/cucumber-reports/cucumber-html-report.html}</li>
 *   <li><b>JSON</b> &ndash; {@code target/cucumber-reports/cucumber-report.json}</li>
 *   <li><b>JUnit XML</b> &ndash; {@code target/cucumber-reports/cucumber-junit-report.xml}</li>
 *   <li><b>Pretty</b> &ndash; console output</li>
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "classpath:features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.springframework.samples.petclinic.customers.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value =
    "pretty, " +
    "html:target/cucumber-reports/cucumber-html-report.html, " +
    "json:target/cucumber-reports/cucumber-report.json, " +
    "junit:target/cucumber-reports/cucumber-junit-report.xml"
)
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
public class CucumberRunnerTest {
    // Intentionally empty -- Cucumber discovers and runs scenarios via the annotations above.
}
