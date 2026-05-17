package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

    @Test
    void gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void defaultIdIsNull() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
    }

    @Test
    void getSpecialtiesReturnsEmptyListWhenNone() {
        Vet vet = new Vet();
        List<Specialty> specialties = vet.getSpecialties();
        assertThat(specialties).isEmpty();
    }

    @Test
    void addSpecialtyIncrementsCount() {
        Vet vet = new Vet();
        Specialty s1 = new Specialty();
        s1.setName("radiology");

        vet.addSpecialty(s1);

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void specialtiesAreSortedByName() {
        Vet vet = new Vet();

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        vet.addSpecialty(surgery);

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        vet.addSpecialty(dentistry);

        List<Specialty> specs = vet.getSpecialties();
        assertThat(specs).hasSize(2);
        assertThat(specs.get(0).getName()).isEqualTo("dentistry");
        assertThat(specs.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void nrOfSpecialtiesIsZeroWhenNone() {
        Vet vet = new Vet();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }
}
