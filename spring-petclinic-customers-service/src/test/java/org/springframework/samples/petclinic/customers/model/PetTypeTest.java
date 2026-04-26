package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    @Test
    void shouldSetAndGetId() {
        PetType petType = new PetType();
        petType.setId(1);
        assertThat(petType.getId()).isEqualTo(1);
    }

    @Test
    void shouldSetAndGetName() {
        PetType petType = new PetType();
        petType.setName("cat");
        assertThat(petType.getName()).isEqualTo("cat");
    }
}
