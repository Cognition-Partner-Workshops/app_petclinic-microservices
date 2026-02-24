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

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Reusable Cucumber step definitions for HTTP operations.
 * These steps can be shared across all microservice test suites.
 *
 * <p>Usage in feature files:</p>
 * <pre>
 *   Given the base URL is "/owners"
 *   When I send a GET request to "/owners"
 *   Then the response status should be 200
 * </pre>
 */
public class HttpStepDefinitions {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScenarioContext context;

	@Given("the base URL is {string}")
	public void theBaseUrlIs(String baseUrl) {
		context.setBaseUrl(baseUrl);
	}

	@When("I send a GET request to {string}")
	public void iSendAGetRequestTo(String path) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get(path)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I send a POST request to {string} with body:")
	public void iSendAPostRequestToWithBody(String path, String body) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.post(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I send a PUT request to {string} with body:")
	public void iSendAPutRequestToWithBody(String path, String body) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.put(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@When("I send a DELETE request to {string}")
	public void iSendADeleteRequestTo(String path) throws Exception {
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.delete(path)
				.accept(MediaType.APPLICATION_JSON));
		MvcResult mvcResult = result.andReturn();
		context.setLastResult(mvcResult);
		context.setLastResultActions(result);
	}

	@Then("the response status should be {int}")
	public void theResponseStatusShouldBe(int expectedStatus) throws Exception {
		context.getLastResultActions().andExpect(status().is(expectedStatus));
	}

	@Then("the response content type should be {string}")
	public void theResponseContentTypeShouldBe(String contentType) {
		String actualContentType = context.getLastResult().getResponse().getContentType();
		if (actualContentType == null || !actualContentType.contains(contentType)) {
			throw new AssertionError(
				"Expected content type containing '" + contentType + "' but got '" + actualContentType + "'");
		}
	}

}
