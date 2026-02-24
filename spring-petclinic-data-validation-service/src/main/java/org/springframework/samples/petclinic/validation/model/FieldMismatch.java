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
 * Represents a single field-level mismatch between source and destination data.
 */
public class FieldMismatch {

	private int rowIndex;

	private String fieldName;

	private String sourceValue;

	private String destinationValue;

	public FieldMismatch() {
	}

	public FieldMismatch(int rowIndex, String fieldName, String sourceValue, String destinationValue) {
		this.rowIndex = rowIndex;
		this.fieldName = fieldName;
		this.sourceValue = sourceValue;
		this.destinationValue = destinationValue;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getDestinationValue() {
		return destinationValue;
	}

	public void setDestinationValue(String destinationValue) {
		this.destinationValue = destinationValue;
	}

}
