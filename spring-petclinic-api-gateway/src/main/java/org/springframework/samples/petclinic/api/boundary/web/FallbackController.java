package org.springframework.samples.petclinic.api.boundary.web;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fallback controller for the API Gateway's circuit breaker.
 * <p>
 * Provides a user-friendly error response when downstream services (e.g. the GenAI
 * chat service) are unavailable. The gateway's circuit breaker routes are configured
 * to forward requests here when the target service cannot be reached.
 */
@RestController
public class FallbackController {

    /**
     * Handles fallback POST requests and returns a 503 Service Unavailable response.
     *
     * @return a {@link ResponseEntity} with HTTP 503 status and a descriptive error message
     */
    @PostMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
                .body("Chat is currently unavailable. Please try again later.");
    }
}
