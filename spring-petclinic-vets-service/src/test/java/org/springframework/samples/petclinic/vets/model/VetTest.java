package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VetTest {

    @Test
    void shouldExposeSpecialtiesSortedByName() {
        Vet vet = new Vet();
        vet.addSpecialty(specialty("surgery"));
        vet.addSpecialty(specialty("dentistry"));

        assertThat(vet.getSpecialties()).extracting(Specialty::getName)
            .containsExactly("dentistry", "surgery");
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
    }

    @Test
    void shouldHaveNoSpecialtiesByDefault() {
        Vet vet = new Vet();

        assertThat(vet.getSpecialties()).isEmpty();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }

    @Test
    void shouldReturnUnmodifiableSpecialtyList() {
        Vet vet = new Vet();

        assertThatThrownBy(() -> vet.getSpecialties().add(specialty("radiology")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldExposeIdentityAttributes() {
        Vet vet = new Vet();
        vet.setId(7);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        assertThat(vet.getId()).isEqualTo(7);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void specialtyShouldExposeIdAndName() {
        Specialty specialty = specialty("radiology");

        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getId()).isNull();
    }

    private Specialty specialty(String name) {
        Specialty specialty = new Specialty();
        specialty.setName(name);
        return specialty;
    }
}
