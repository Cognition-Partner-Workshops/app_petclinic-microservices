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
package org.springframework.samples.petclinic.validation.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.validation.model.ComparisonType;
import org.springframework.samples.petclinic.validation.model.ValidationReport;
import org.springframework.samples.petclinic.validation.model.ValidationRequest;
import org.springframework.samples.petclinic.validation.service.DbToDbComparisonService;
import org.springframework.samples.petclinic.validation.service.FileToApiComparisonService;
import org.springframework.samples.petclinic.validation.service.FileToDbComparisonService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the data validation endpoint.
 * Accepts a comparison type and request parameters, delegates to the
 * appropriate comparison service, and returns a {@link ValidationReport}.
 */
@RestController
public class ValidationController {

	private final DbToDbComparisonService dbToDbService;

	private final FileToDbComparisonService fileToDbService;

	private final FileToApiComparisonService fileToApiService;

	public ValidationController(DbToDbComparisonService dbToDbService, FileToDbComparisonService fileToDbService,
			FileToApiComparisonService fileToApiService) {
		this.dbToDbService = dbToDbService;
		this.fileToDbService = fileToDbService;
		this.fileToApiService = fileToApiService;
	}

	/**
	 * Validates legacy/source data against modern/destination data.
	 * @param comparisonType one of: DB_TO_DB, FILE_TO_DB, FILE_TO_API
	 * @param request the validation request parameters
	 * @return a validation report with match/mismatch counts, sample mismatches, and pass/fail status
	 */
	@PostMapping("/validate/{comparison-type}")
	public ResponseEntity<ValidationReport> validate(@PathVariable("comparison-type") String comparisonType,
			@RequestBody ValidationRequest request) {

		ComparisonType type;
		try {
			type = ComparisonType.valueOf(comparisonType.toUpperCase().replace("-", "_"));
		}
		catch (IllegalArgumentException ex) {
			ValidationReport errorReport = new ValidationReport();
			errorReport.setStatus("ERROR");
			errorReport.setPassed(false);
			errorReport.setErrorMessage(
					"Unknown comparison type: '" + comparisonType + "'. Supported types: DB_TO_DB, FILE_TO_DB, FILE_TO_API");
			return ResponseEntity.badRequest().body(errorReport);
		}

		ValidationReport report;
		switch (type) {
			case DB_TO_DB -> report = dbToDbService.compare(request);
			case FILE_TO_DB -> report = fileToDbService.compare(request);
			case FILE_TO_API -> report = fileToApiService.compare(request);
			default -> {
				ValidationReport unknownReport = new ValidationReport();
				unknownReport.setStatus("ERROR");
				unknownReport.setPassed(false);
				unknownReport.setErrorMessage("Unsupported comparison type: " + type);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(unknownReport);
			}
		}

		HttpStatus status = "ERROR".equals(report.getStatus()) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
		return ResponseEntity.status(status).body(report);
	}

}
