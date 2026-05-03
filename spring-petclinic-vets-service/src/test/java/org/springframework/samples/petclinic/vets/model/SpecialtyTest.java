package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void gettersAndSetters() {
        Specialty specialty = new Specialty();
        specialty.setName("dentistry");

        assertThat(specialty.getName()).isEqualTo("dentistry");
        assertThat(specialty.getId()).isNull();
    }

    @Test
    void defaultValuesAreNull() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
        assertThat(specialty.getName()).isNull();
    }
}
