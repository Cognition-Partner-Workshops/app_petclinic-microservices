package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    @Test
    void shouldCreateRecord() {
        PetType type = new PetType("cat");
        assertThat(type.name()).isEqualTo("cat");
    }
}
