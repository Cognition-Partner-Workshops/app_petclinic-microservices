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
package org.springframework.samples.petclinic.validation.model;

/**
 * Defines a single field within a fixed-width mainframe file layout.
 * Used to parse fields from lines of a mainframe output file.
 */
public class FieldDefinition {

	private String name;

	private int startPosition;

	private int length;

	private String targetFieldName;

	public FieldDefinition() {
	}

	public FieldDefinition(String name, int startPosition, int length, String targetFieldName) {
		this.name = name;
		this.startPosition = startPosition;
		this.length = length;
		this.targetFieldName = targetFieldName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getTargetFieldName() {
		return targetFieldName;
	}

	public void setTargetFieldName(String targetFieldName) {
		this.targetFieldName = targetFieldName;
	}

}
