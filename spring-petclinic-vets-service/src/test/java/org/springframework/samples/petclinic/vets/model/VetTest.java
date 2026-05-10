package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void shouldGetAndSetFields() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldReturnEmptySpecialtiesWhenNoneAdded() {
        Vet vet = new Vet();
        assertThat(vet.getSpecialties()).isEmpty();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }

    @Test
    void shouldAddSpecialty() {
        Vet vet = new Vet();
        Specialty spec = new Specialty();
        spec.setName("radiology");

        vet.addSpecialty(spec);

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
        assertThat(vet.getSpecialties()).extracting(Specialty::getName).containsExactly("radiology");
    }

    @Test
    void shouldReturnSortedSpecialties() {
        Vet vet = new Vet();
        Specialty specC = new Specialty();
        specC.setName("surgery");
        Specialty specA = new Specialty();
        specA.setName("dentistry");
        Specialty specB = new Specialty();
        specB.setName("radiology");

        vet.addSpecialty(specC);
        vet.addSpecialty(specA);
        vet.addSpecialty(specB);

        assertThat(vet.getSpecialties()).extracting(Specialty::getName)
            .containsExactly("dentistry", "radiology", "surgery");
        assertThat(vet.getNrOfSpecialties()).isEqualTo(3);
    }

    @Test
    void shouldReturnNullIdWhenNotSet() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
    }
}
