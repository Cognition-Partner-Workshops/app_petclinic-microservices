package org.springframework.samples.petclinic.customers.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform Suite entry point for running Cucumber tests in the customers-service.
 * Scans for .feature files under src/test/resources/features and uses glue code from
 * both the shared test-common steps and the service-specific steps.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
	key = GLUE_PROPERTY_NAME,
	value = "org.springframework.samples.petclinic.test.steps,"
		+ "org.springframework.samples.petclinic.test.data,"
		+ "org.springframework.samples.petclinic.customers.cucumber")
@ConfigurationParameter(
	key = PLUGIN_PROPERTY_NAME,
	value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class RunCucumberTest {

}
