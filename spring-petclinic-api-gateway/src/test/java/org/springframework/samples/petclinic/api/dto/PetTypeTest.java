package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    @Test
    void recordAccessor() {
        PetType type = new PetType("Dog");
        assertThat(type.name()).isEqualTo("Dog");
    }
}
