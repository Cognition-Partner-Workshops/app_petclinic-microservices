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
        owner.setFirstName("John");

        pet.setId(1);
        pet.setName("Buddy");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        assertThat(pet.getId()).isEqualTo(1);
        assertThat(pet.getName()).isEqualTo("Buddy");
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
        assertThat(pet.getType()).isSameAs(type);
        assertThat(pet.getOwner()).isSameAs(owner);
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

    @Test
    void toStringContainsPetFields() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        PetType type = new PetType();
        type.setName("Cat");

        Pet pet = new Pet();
        pet.setId(5);
        pet.setName("Leo");
        pet.setBirthDate(new Date());
        pet.setType(type);
        pet.setOwner(owner);

        String str = pet.toString();
        assertThat(str).contains("Leo");
        assertThat(str).contains("Cat");
        assertThat(str).contains("George");
        assertThat(str).contains("Bush");
    }

    @Test
    void equalsAndHashCode() {
        Date date = new Date();
        PetType type = new PetType();
        type.setName("Dog");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setBirthDate(date);
        pet1.setType(type);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");
        pet2.setBirthDate(date);
        pet2.setType(type);

        assertThat(pet1).isEqualTo(pet2);
        assertThat(pet1.hashCode()).isEqualTo(pet2.hashCode());
    }

    @Test
    void equalsReturnsFalseForDifferentPets() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Max");

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
    void equalsSameInstance() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isEqualTo(pet);
    }

    @Test
    void equalsWithDifferentName() {
        Date date = new Date();
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setBirthDate(date);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Max");
        pet2.setBirthDate(date);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsWithDifferentBirthDate() {
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
    void equalsWithDifferentType() {
        PetType cat = new PetType();
        cat.setName("Cat");
        PetType dog = new PetType();
        dog.setName("Dog");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setType(cat);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");
        pet2.setType(dog);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void equalsWithDifferentOwner() {
        Owner owner1 = new Owner();
        owner1.setFirstName("John");
        Owner owner2 = new Owner();
        owner2.setFirstName("Jane");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Buddy");
        pet1.setOwner(owner1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Buddy");
        pet2.setOwner(owner2);

        assertThat(pet1).isNotEqualTo(pet2);
    }
}
