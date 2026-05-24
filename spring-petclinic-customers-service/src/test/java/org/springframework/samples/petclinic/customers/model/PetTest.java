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
        type.setName("dog");
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        pet.setId(1);
        pet.setName("Buddy");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        assertThat(pet.getId()).isEqualTo(1);
        assertThat(pet.getName()).isEqualTo("Buddy");
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
        assertThat(pet.getType()).isEqualTo(type);
        assertThat(pet.getOwner()).isEqualTo(owner);
    }

    @Test
    void equalsAndHashCodeSameValues() {
        Date birthDate = new Date();
        PetType type = new PetType();
        type.setName("dog");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setBirthDate(birthDate);
        pet1.setType(type);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");
        pet2.setBirthDate(birthDate);
        pet2.setType(type);

        assertThat(pet1).isEqualTo(pet2);
        assertThat(pet1.hashCode()).isEqualTo(pet2.hashCode());
    }

    @Test
    void equalsReturnsFalseForDifferentId() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Buddy");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForDifferentName() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Rex");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForNull() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isNotEqualTo(null);
    }

    @Test
    void equalsReturnsFalseForDifferentClass() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isNotEqualTo("not a pet");
    }

    @Test
    void toStringContainsPetInfo() {
        PetType type = new PetType();
        type.setName("dog");

        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Buddy");
        pet.setBirthDate(new Date());
        pet.setType(type);
        pet.setOwner(owner);

        String str = pet.toString();
        assertThat(str).contains("Buddy");
        assertThat(str).contains("dog");
        assertThat(str).contains("George");
        assertThat(str).contains("Franklin");
    }

    @Test
    void equalsReturnsFalseForDifferentBirthDate() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setBirthDate(new Date(1000));

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");
        pet2.setBirthDate(new Date(2000));

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsReturnsFalseForDifferentType() {
        PetType type1 = new PetType();
        type1.setId(1);
        type1.setName("dog");
        PetType type2 = new PetType();
        type2.setId(2);
        type2.setName("cat");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setType(type1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");
        pet2.setType(type2);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void defaultValuesAreNull() {
        Pet pet = new Pet();
        assertThat(pet.getId()).isNull();
        assertThat(pet.getName()).isNull();
        assertThat(pet.getBirthDate()).isNull();
        assertThat(pet.getType()).isNull();
        assertThat(pet.getOwner()).isNull();
    }
}
