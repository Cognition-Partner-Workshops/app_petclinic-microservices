package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    @Test
    void gettersAndSetters() {
        Pet pet = new Pet();
        Date birthDate = new Date();
        PetType type = new PetType();
        type.setName("Dog");
        Owner owner = new Owner();

        pet.setId(1);
        pet.setName("Rex");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        assertThat(pet.getId()).isEqualTo(1);
        assertThat(pet.getName()).isEqualTo("Rex");
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
        assertThat(pet.getType()).isSameAs(type);
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void equalsAndHashCodeForEqualPets() {
        Date birthDate = new Date();
        PetType type = new PetType();
        type.setName("Cat");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Garfield");
        pet1.setBirthDate(birthDate);
        pet1.setType(type);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Garfield");
        pet2.setBirthDate(birthDate);
        pet2.setType(type);

        assertThat(pet1).isEqualTo(pet2);
        assertThat(pet1.hashCode()).isEqualTo(pet2.hashCode());
    }

    @Test
    void equalsReturnsFalseForDifferentId() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Rex");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Rex");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForDifferentName() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Rex");

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForDifferentBirthDate() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Rex");
        pet1.setBirthDate(new Date(1000));

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Rex");
        pet2.setBirthDate(new Date(2000));

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForDifferentType() {
        PetType type1 = new PetType();
        type1.setName("Dog");
        PetType type2 = new PetType();
        type2.setName("Cat");

        Date d = new Date();
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Rex");
        pet1.setBirthDate(d);
        pet1.setType(type1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Rex");
        pet2.setBirthDate(d);
        pet2.setType(type2);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForDifferentOwner() {
        Owner owner1 = new Owner();
        owner1.setFirstName("John");
        Owner owner2 = new Owner();
        owner2.setFirstName("Jane");

        PetType type = new PetType();
        type.setName("Dog");
        Date d = new Date();

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Rex");
        pet1.setBirthDate(d);
        pet1.setType(type);
        pet1.setOwner(owner1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Rex");
        pet2.setBirthDate(d);
        pet2.setType(type);
        pet2.setOwner(owner2);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForNull() {
        Pet pet = new Pet();
        assertThat(pet).isNotEqualTo(null);
    }

    @Test
    void equalsReturnsFalseForDifferentClass() {
        Pet pet = new Pet();
        assertThat(pet).isNotEqualTo("not a pet");
    }

    @Test
    void toStringContainsFields() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");

        PetType type = new PetType();
        type.setName("Dog");

        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Rex");
        pet.setBirthDate(new Date());
        pet.setType(type);
        pet.setOwner(owner);

        String result = pet.toString();
        assertThat(result).contains("Rex");
        assertThat(result).contains("Dog");
        assertThat(result).contains("John");
        assertThat(result).contains("Doe");
    }
}
