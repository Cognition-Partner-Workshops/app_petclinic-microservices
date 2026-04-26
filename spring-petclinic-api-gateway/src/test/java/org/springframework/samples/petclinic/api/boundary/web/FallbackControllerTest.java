package org.springframework.samples.petclinic.api.boundary.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class FallbackControllerTest {

    private final FallbackController controller = new FallbackController();

    @Test
    void shouldReturn503WithMessage() {
        ResponseEntity<String> response = controller.fallback();

        assertThat(response.getStatusCode().value()).isEqualTo(503);
        assertThat(response.getBody()).contains("Chat is currently unavailable");
    }
}
