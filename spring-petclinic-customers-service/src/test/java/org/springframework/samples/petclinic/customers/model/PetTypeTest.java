package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    @Test
    void gettersAndSetters() {
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("dog");

        assertThat(petType.getId()).isEqualTo(1);
        assertThat(petType.getName()).isEqualTo("dog");
    }

    @Test
    void defaultValuesAreNull() {
        PetType petType = new PetType();
        assertThat(petType.getId()).isNull();
        assertThat(petType.getName()).isNull();
    }
}
