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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reusable Cucumber step definitions for database setup and verification.
 * Supports executing SQL statements, verifying row counts, and cleaning tables.
 *
 * <p>Usage in feature files:</p>
 * <pre>
 *   Given the database table "owners" has been cleared
 *   Given I execute SQL "INSERT INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023')"
 *   Then the database table "owners" should have {int} rows
 * </pre>
 */
public class DatabaseStepDefinitions {

	@Autowired(required = false)
	private JdbcTemplate jdbcTemplate;

	@Given("the database table {string} has been cleared")
	public void theDatabaseTableHasBeenCleared(String tableName) {
		requireJdbcTemplate();
		jdbcTemplate.execute("DELETE FROM " + tableName);
	}

	@Given("I execute SQL {string}")
	public void iExecuteSql(String sql) {
		requireJdbcTemplate();
		jdbcTemplate.execute(sql);
	}

	@Given("I execute SQL:")
	public void iExecuteSqlDocString(String sql) {
		requireJdbcTemplate();
		for (String statement : sql.split(";")) {
			String trimmed = statement.trim();
			if (!trimmed.isEmpty()) {
				jdbcTemplate.execute(trimmed);
			}
		}
	}

	@Then("the database table {string} should have {int} rows")
	public void theDatabaseTableShouldHaveRows(String tableName, int expectedCount) {
		requireJdbcTemplate();
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
		assertThat(count).as("Row count for table '%s'", tableName).isEqualTo(expectedCount);
	}

	@Then("the database table {string} should have at least {int} rows")
	public void theDatabaseTableShouldHaveAtLeastRows(String tableName, int minCount) {
		requireJdbcTemplate();
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
		assertThat(count).as("Row count for table '%s'", tableName).isGreaterThanOrEqualTo(minCount);
	}

	@Then("the database query {string} should return {int} rows")
	public void theDatabaseQueryShouldReturnRows(String query, int expectedCount) {
		requireJdbcTemplate();
		Integer count = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM (" + query + ") AS sub", Integer.class);
		assertThat(count).isEqualTo(expectedCount);
	}

	private void requireJdbcTemplate() {
		if (jdbcTemplate == null) {
			throw new IllegalStateException(
				"JdbcTemplate is not available. Ensure a DataSource is configured for this test context.");
		}
	}

}
