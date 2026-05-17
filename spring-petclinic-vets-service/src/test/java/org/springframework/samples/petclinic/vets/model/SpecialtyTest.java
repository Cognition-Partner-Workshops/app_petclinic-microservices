package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    @Test
    void gettersAndSetters() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");

        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void idIsNullByDefault() {
        Specialty specialty = new Specialty();
        assertThat(specialty.getId()).isNull();
    }
}
