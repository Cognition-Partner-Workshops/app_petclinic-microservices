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

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Shared scenario context that holds state between Cucumber steps within a single scenario.
 * This bean is scenario-scoped via Cucumber-Spring's glue configuration.
 *
 * <p>Stores the last HTTP result, base URL, and any shared data needed across steps.</p>
 */
@Component
public class ScenarioContext {

	private MvcResult lastResult;

	private ResultActions lastResultActions;

	private String baseUrl = "";

	private String lastResponseBody;

	public MvcResult getLastResult() {
		return lastResult;
	}

	public void setLastResult(MvcResult lastResult) {
		this.lastResult = lastResult;
		try {
			this.lastResponseBody = lastResult.getResponse().getContentAsString();
		}
		catch (Exception e) {
			this.lastResponseBody = "";
		}
	}

	public ResultActions getLastResultActions() {
		return lastResultActions;
	}

	public void setLastResultActions(ResultActions lastResultActions) {
		this.lastResultActions = lastResultActions;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getLastResponseBody() {
		return lastResponseBody;
	}

	/**
	 * Resets all state for a new scenario.
	 */
	public void reset() {
		this.lastResult = null;
		this.lastResultActions = null;
		this.baseUrl = "";
		this.lastResponseBody = null;
	}

}
