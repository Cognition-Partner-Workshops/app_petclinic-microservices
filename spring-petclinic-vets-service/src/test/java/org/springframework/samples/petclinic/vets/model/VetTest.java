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
    void shouldGetSpecialtiesReturnEmptyWhenNone() {
        Vet vet = new Vet();
        assertThat(vet.getSpecialties()).isEmpty();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }

    @Test
    void shouldAddSpecialty() {
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setName("radiology");

        vet.addSpecialty(specialty);

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
        assertThat(vet.getSpecialties()).extracting(Specialty::getName).containsExactly("radiology");
    }

    @Test
    void shouldReturnSpecialtiesSortedByName() {
        Vet vet = new Vet();
        Specialty s1 = new Specialty();
        s1.setName("surgery");
        Specialty s2 = new Specialty();
        s2.setName("dentistry");

        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        assertThat(vet.getSpecialties()).extracting(Specialty::getName)
            .containsExactly("dentistry", "surgery");
    }
}
