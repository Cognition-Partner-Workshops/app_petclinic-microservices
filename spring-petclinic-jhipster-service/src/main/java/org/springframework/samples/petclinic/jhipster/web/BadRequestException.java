package org.springframework.samples.petclinic.jhipster.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown for bad request scenarios (validation failures, duplicate IDs, etc.).
 * Replaces JHipster's BadRequestAlertException with a simpler petclinic-style approach.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private final String entityName;
    private final String errorKey;

    public BadRequestException(String message, String entityName, String errorKey) {
        super(message);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
