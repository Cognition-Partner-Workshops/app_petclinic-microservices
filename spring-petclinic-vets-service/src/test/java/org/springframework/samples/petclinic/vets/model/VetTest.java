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
    void getSpecialtiesReturnsEmptyListWhenNone() {
        Vet vet = new Vet();
        List<Specialty> specialties = vet.getSpecialties();
        assertThat(specialties).isEmpty();
    }

    @Test
    void addSpecialtyIncrementsCount() {
        Vet vet = new Vet();
        Specialty s = new Specialty();
        s.setName("radiology");
        vet.addSpecialty(s);

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtiesReturnsSortedByName() {
        Vet vet = new Vet();

        Specialty s1 = new Specialty();
        s1.setName("surgery");
        vet.addSpecialty(s1);

        Specialty s2 = new Specialty();
        s2.setName("dentistry");
        vet.addSpecialty(s2);

        Specialty s3 = new Specialty();
        s3.setName("radiology");
        vet.addSpecialty(s3);

        List<Specialty> specs = vet.getSpecialties();
        assertThat(specs).extracting(Specialty::getName)
            .containsExactly("dentistry", "radiology", "surgery");
    }

    @Test
    void getSpecialtiesReturnsUnmodifiableList() {
        Vet vet = new Vet();
        Specialty s = new Specialty();
        s.setName("radiology");
        vet.addSpecialty(s);

        List<Specialty> specs = vet.getSpecialties();
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
            () -> specs.add(new Specialty()));
    }

    @Test
    void getNrOfSpecialtiesWhenNullSpecialtiesInitializesSet() {
        Vet vet = new Vet();
        assertThat(vet.getNrOfSpecialties()).isEqualTo(0);
    }

    @Test
    void defaultIdIsNull() {
        Vet vet = new Vet();
        assertThat(vet.getId()).isNull();
    }
}
