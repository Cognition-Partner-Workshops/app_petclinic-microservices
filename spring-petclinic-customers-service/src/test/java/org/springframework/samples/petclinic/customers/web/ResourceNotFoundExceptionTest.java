package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldCarryMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Owner 1 not found");
        assertThat(ex.getMessage()).isEqualTo("Owner 1 not found");
    }

    @Test
    void isRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
