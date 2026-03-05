package org.springframework.samples.petclinic.customers.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource (e.g. an owner or pet) cannot be found.
 * <p>
 * Automatically mapped to an HTTP 404 Not Found response by Spring's
 * {@link ResponseStatus} annotation.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message a human-readable description of the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
