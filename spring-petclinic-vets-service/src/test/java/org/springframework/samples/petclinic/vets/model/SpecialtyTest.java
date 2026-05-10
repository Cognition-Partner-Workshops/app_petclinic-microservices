package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void shouldGetAndSetName() {
        Specialty specialty = new Specialty();
        specialty.setName("surgery");
        assertThat(specialty.getName()).isEqualTo("surgery");
    }

    @Test
    void shouldReturnNullForUnsetFields() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
    }
}
