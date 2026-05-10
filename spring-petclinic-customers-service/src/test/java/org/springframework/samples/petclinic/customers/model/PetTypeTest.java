package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    @Test
    void shouldGetAndSetId() {
        PetType petType = new PetType();
        petType.setId(1);
        assertThat(petType.getId()).isEqualTo(1);
    }

    @Test
    void shouldGetAndSetName() {
        PetType petType = new PetType();
        petType.setName("cat");
        assertThat(petType.getName()).isEqualTo("cat");
    }

    @Test
    void shouldReturnNullForUnsetFields() {
        PetType petType = new PetType();
        assertThat(petType.getId()).isNull();
        assertThat(petType.getName()).isNull();
    }
}
