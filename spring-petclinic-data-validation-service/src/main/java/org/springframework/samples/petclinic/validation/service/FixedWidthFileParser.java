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
package org.springframework.samples.petclinic.validation.service;

import org.springframework.samples.petclinic.validation.model.FieldDefinition;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses fixed-width mainframe output files (e.g., CardDemo ASCII exports)
 * into a list of row maps based on {@link FieldDefinition} layouts.
 */
@Component
public class FixedWidthFileParser {

	/**
	 * Parses a fixed-width file into a list of maps, where each map represents
	 * one row with field names as keys and trimmed field values as values.
	 * @param filePath path to the fixed-width file
	 * @param fieldDefinitions layout definitions for each field
	 * @param skipHeaderLines number of header lines to skip
	 * @param encoding character encoding of the file
	 * @return list of parsed row maps
	 * @throws IOException if the file cannot be read
	 */
	public List<Map<String, String>> parse(Path filePath, List<FieldDefinition> fieldDefinitions,
			int skipHeaderLines, Charset encoding) throws IOException {

		List<String> lines = Files.readAllLines(filePath, encoding);
		List<Map<String, String>> rows = new ArrayList<>();

		for (int i = skipHeaderLines; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.isBlank()) {
				continue;
			}
			Map<String, String> row = parseLine(line, fieldDefinitions);
			rows.add(row);
		}

		return rows;
	}

	/**
	 * Parses a single fixed-width line into a map of field name to value.
	 * @param line the raw line from the file
	 * @param fieldDefinitions layout definitions for each field
	 * @return map of target field name to trimmed value
	 */
	Map<String, String> parseLine(String line, List<FieldDefinition> fieldDefinitions) {
		Map<String, String> row = new LinkedHashMap<>();
		for (FieldDefinition fd : fieldDefinitions) {
			int start = fd.getStartPosition();
			int end = Math.min(start + fd.getLength(), line.length());
			String value = "";
			if (start < line.length()) {
				value = line.substring(start, end).trim();
			}
			String key = fd.getTargetFieldName() != null ? fd.getTargetFieldName() : fd.getName();
			row.put(key, value);
		}
		return row;
	}

}
