package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void messageIsPreserved() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Pet 42 not found");
        assertThat(ex.getMessage()).isEqualTo("Pet 42 not found");
    }
}
