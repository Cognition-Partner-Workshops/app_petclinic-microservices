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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class that loads test data from CSV and YAML files on the classpath.
 * This enables a parameterized test pattern where adding new rows to a CSV file
 * or entries to a YAML file automatically creates new test cases.
 *
 * <h3>CSV Format</h3>
 * <p>The first row is treated as headers (column names). Each subsequent row
 * becomes a {@code Map<String, String>} keyed by header names.</p>
 *
 * <h3>YAML Format</h3>
 * <p>Expects a top-level list of maps. Each map entry represents one test case.</p>
 *
 * <h3>Usage</h3>
 * <pre>
 *   // Load from CSV
 *   List&lt;Map&lt;String, String&gt;&gt; rows = TestDataLoader.loadCsv("testdata/owners.csv");
 *
 *   // Load from YAML
 *   List&lt;Map&lt;String, Object&gt;&gt; scenarios = TestDataLoader.loadYaml("testdata/validation-scenarios.yml");
 * </pre>
 */
public final class TestDataLoader {

	private TestDataLoader() {
		// utility class
	}

	/**
	 * Loads test data from a CSV file on the classpath.
	 * The first row is used as column headers.
	 * @param classpathResource the classpath resource path (e.g., "testdata/owners.csv")
	 * @return a list of maps, one per data row, keyed by header names
	 * @throws IllegalArgumentException if the resource is not found
	 * @throws IllegalStateException if the CSV cannot be parsed
	 */
	public static List<Map<String, String>> loadCsv(String classpathResource) {
		InputStream is = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(classpathResource);
		if (is == null) {
			throw new IllegalArgumentException("CSV resource not found: " + classpathResource);
		}

		try (CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			List<String[]> allRows = reader.readAll();
			if (allRows.isEmpty()) {
				return List.of();
			}

			String[] headers = allRows.get(0);
			List<Map<String, String>> result = new ArrayList<>();

			for (int i = 1; i < allRows.size(); i++) {
				String[] row = allRows.get(i);
				Map<String, String> map = new LinkedHashMap<>();
				for (int j = 0; j < headers.length && j < row.length; j++) {
					map.put(headers[j].trim(), row[j].trim());
				}
				result.add(map);
			}
			return result;
		}
		catch (IOException | CsvException e) {
			throw new IllegalStateException("Failed to parse CSV: " + classpathResource, e);
		}
	}

	/**
	 * Loads test data from a YAML file on the classpath.
	 * Expects a top-level list of maps.
	 * @param classpathResource the classpath resource path (e.g., "testdata/scenarios.yml")
	 * @return a list of maps representing each test scenario
	 * @throws IllegalArgumentException if the resource is not found
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> loadYaml(String classpathResource) {
		InputStream is = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(classpathResource);
		if (is == null) {
			throw new IllegalArgumentException("YAML resource not found: " + classpathResource);
		}

		Yaml yaml = new Yaml();
		Object loaded = yaml.load(is);
		if (loaded instanceof List) {
			return (List<Map<String, Object>>) loaded;
		}
		throw new IllegalStateException(
			"Expected a YAML list at the top level, but got: " + loaded.getClass().getName());
	}

	/**
	 * Convenience method to get a required string value from a test data map.
	 * @param data the test data map
	 * @param key the key to look up
	 * @return the value as a string
	 * @throws IllegalArgumentException if the key is not found
	 */
	public static String requireString(Map<String, ?> data, String key) {
		Object value = data.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Required test data key not found: " + key);
		}
		return String.valueOf(value);
	}

	/**
	 * Convenience method to get an optional string value with a default.
	 * @param data the test data map
	 * @param key the key to look up
	 * @param defaultValue the default if missing
	 * @return the value or defaultValue
	 */
	public static String optionalString(Map<String, ?> data, String key, String defaultValue) {
		Object value = data.get(key);
		return value != null ? String.valueOf(value) : defaultValue;
	}

}
