package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    @Test
    void recordAccessor() {
        PetType petType = new PetType("cat");
        assertThat(petType.name()).isEqualTo("cat");
    }
}
