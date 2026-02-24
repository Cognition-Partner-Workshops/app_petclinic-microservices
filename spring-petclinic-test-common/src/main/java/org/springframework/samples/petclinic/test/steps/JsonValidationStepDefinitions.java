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
package org.springframework.samples.petclinic.test.steps;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reusable Cucumber step definitions for JSON response validation.
 * Supports JSONPath-based field checks, value matching, array size assertions,
 * and null/non-null checks.
 *
 * <p>Usage in feature files:</p>
 * <pre>
 *   Then the JSON response should have field "$.firstName"
 *   And the JSON field "$.firstName" should be "George"
 *   And the JSON array "$.pets" should have size 2
 * </pre>
 */
public class JsonValidationStepDefinitions {

	@Autowired
	private ScenarioContext context;

	@Then("the JSON response should have field {string}")
	public void theJsonResponseShouldHaveField(String jsonPath) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(value).as("Expected field at '%s' to exist", jsonPath).isNotNull();
	}

	@Then("the JSON field {string} should be {string}")
	public void theJsonFieldShouldBe(String jsonPath, String expectedValue) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(String.valueOf(value)).isEqualTo(expectedValue);
	}

	@Then("the JSON field {string} should be the integer {int}")
	public void theJsonFieldShouldBeInteger(String jsonPath, int expectedValue) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(((Number) value).intValue()).isEqualTo(expectedValue);
	}

	@Then("the JSON field {string} should be true")
	public void theJsonFieldShouldBeTrue(String jsonPath) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(value).isEqualTo(true);
	}

	@Then("the JSON field {string} should be false")
	public void theJsonFieldShouldBeFalse(String jsonPath) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(value).isEqualTo(false);
	}

	@Then("the JSON field {string} should be null")
	public void theJsonFieldShouldBeNull(String jsonPath) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(value).isNull();
	}

	@Then("the JSON field {string} should not be null")
	public void theJsonFieldShouldNotBeNull(String jsonPath) {
		String body = context.getLastResponseBody();
		Object value = JsonPath.read(body, jsonPath);
		assertThat(value).isNotNull();
	}

	@Then("the JSON array {string} should have size {int}")
	public void theJsonArrayShouldHaveSize(String jsonPath, int expectedSize) {
		String body = context.getLastResponseBody();
		JSONArray array = JsonPath.read(body, jsonPath);
		assertThat(array).hasSize(expectedSize);
	}

	@Then("the JSON array {string} should not be empty")
	public void theJsonArrayShouldNotBeEmpty(String jsonPath) {
		String body = context.getLastResponseBody();
		JSONArray array = JsonPath.read(body, jsonPath);
		assertThat(array).isNotEmpty();
	}

	@Then("the JSON array {string} should be empty")
	public void theJsonArrayShouldBeEmpty(String jsonPath) {
		String body = context.getLastResponseBody();
		JSONArray array = JsonPath.read(body, jsonPath);
		assertThat(array).isEmpty();
	}

	@Then("the JSON response should contain {string}")
	public void theJsonResponseShouldContain(String expectedSubstring) {
		String body = context.getLastResponseBody();
		assertThat(body).contains(expectedSubstring);
	}

}
