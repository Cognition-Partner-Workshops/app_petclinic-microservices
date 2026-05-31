package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    @Test
    void shouldSetAndGetFields() {
        Pet pet = new Pet();
        Date birthDate = new Date();
        PetType type = new PetType();
        type.setId(1);
        type.setName("cat");
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        pet.setId(1);
        pet.setName("Leo");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        assertThat(pet.getId()).isEqualTo(1);
        assertThat(pet.getName()).isEqualTo("Leo");
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
        assertThat(pet.getType()).isEqualTo(type);
        assertThat(pet.getOwner()).isEqualTo(owner);
    }

    @Test
    void shouldToStringContainPetDetails() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");
        pet.setBirthDate(new Date());
        PetType type = new PetType();
        type.setName("cat");
        pet.setType(type);
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        pet.setOwner(owner);

        String result = pet.toString();

        assertThat(result).contains("Leo");
        assertThat(result).contains("cat");
    }

    @Test
    void shouldEqualsSameValues() {
        Date date = new Date();
        PetType type = new PetType();
        type.setId(1);

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(date);
        pet1.setType(type);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");
        pet2.setBirthDate(date);
        pet2.setType(type);

        assertThat(pet1).isEqualTo(pet2);
        assertThat(pet1.hashCode()).isEqualTo(pet2.hashCode());
    }

    @Test
    void shouldNotEqualDifferentId() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Leo");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotEqualNull() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isNotEqualTo(null);
    }

    @Test
    void shouldNotEqualDifferentClass() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isNotEqualTo("not a pet");
    }

    @Test
    void shouldNotEqualDifferentName() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Max");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotEqualDifferentBirthDate() {
        Date date1 = new Date(1000);
        Date date2 = new Date(2000);

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(date1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");
        pet2.setBirthDate(date2);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotEqualDifferentType() {
        PetType type1 = new PetType();
        type1.setId(1);
        PetType type2 = new PetType();
        type2.setId(2);

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setType(type1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");
        pet2.setType(type2);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotEqualDifferentOwner() {
        Owner owner1 = new Owner();
        owner1.setFirstName("A");
        owner1.setLastName("B");
        Owner owner2 = new Owner();
        owner2.setFirstName("C");
        owner2.setLastName("D");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setOwner(owner1);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");
        pet2.setOwner(owner2);

        assertThat(pet1).isNotEqualTo(pet2);
    }
}
