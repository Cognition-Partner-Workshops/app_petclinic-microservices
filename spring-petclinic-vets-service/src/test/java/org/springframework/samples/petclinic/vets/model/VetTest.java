package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void shouldSetAndGetFields() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldReturnEmptySpecialtiesWhenNone() {
        Vet vet = new Vet();
        assertThat(vet.getSpecialties()).isEmpty();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }

    @Test
    void shouldAddSpecialty() {
        Vet vet = new Vet();
        Specialty radiology = new Specialty();
        radiology.setName("radiology");

        vet.addSpecialty(radiology);

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldReturnSpecialtiesSortedByName() {
        Vet vet = new Vet();

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");

        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);

        assertThat(vet.getSpecialties()).extracting(Specialty::getName)
            .containsExactly("dentistry", "surgery");
    }

    @Test
    void shouldHandleNullSpecialtiesGracefully() {
        Vet vet = new Vet();
        assertThat(vet.getNrOfSpecialties()).isZero();
        vet.addSpecialty(new Specialty());
        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
    }
}
