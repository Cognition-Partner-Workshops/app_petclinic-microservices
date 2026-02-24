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
package org.springframework.samples.petclinic.test.data;

import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.test.steps.ScenarioContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber step definitions for loading and validating parameterized test data
 * from CSV and YAML files.
 *
 * <p>Usage in feature files:</p>
 * <pre>
 *   Given test data is loaded from CSV "testdata/owners.csv"
 *   Given test data is loaded from YAML "testdata/validation-scenarios.yml"
 *   Then the loaded test data should have {int} entries
 * </pre>
 */
public class TestDataStepDefinitions {

	@Autowired
	private ScenarioContext context;

	private List<Map<String, String>> csvData;

	private List<Map<String, Object>> yamlData;

	@Given("test data is loaded from CSV {string}")
	public void testDataIsLoadedFromCsv(String csvPath) {
		csvData = TestDataLoader.loadCsv(csvPath);
		assertThat(csvData).as("CSV data from '%s'", csvPath).isNotEmpty();
	}

	@Given("test data is loaded from YAML {string}")
	public void testDataIsLoadedFromYaml(String yamlPath) {
		yamlData = TestDataLoader.loadYaml(yamlPath);
		assertThat(yamlData).as("YAML data from '%s'", yamlPath).isNotEmpty();
	}

	@Given("the loaded CSV data should have {int} entries")
	public void theLoadedCsvDataShouldHaveEntries(int expectedCount) {
		assertThat(csvData).hasSize(expectedCount);
	}

	@Given("the loaded YAML data should have {int} entries")
	public void theLoadedYamlDataShouldHaveEntries(int expectedCount) {
		assertThat(yamlData).hasSize(expectedCount);
	}

	public List<Map<String, String>> getCsvData() {
		return csvData;
	}

	public List<Map<String, Object>> getYamlData() {
		return yamlData;
	}

}
