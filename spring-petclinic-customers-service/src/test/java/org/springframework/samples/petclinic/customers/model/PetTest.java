package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    @Test
    void shouldGetAndSetFields() {
        Pet pet = new Pet();
        Date birthDate = new Date();
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("cat");
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        pet.setId(1);
        pet.setName("Leo");
        pet.setBirthDate(birthDate);
        pet.setType(petType);
        pet.setOwner(owner);

        assertThat(pet.getId()).isEqualTo(1);
        assertThat(pet.getName()).isEqualTo("Leo");
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
        assertThat(pet.getType()).isEqualTo(petType);
        assertThat(pet.getOwner()).isEqualTo(owner);
    }

    @Test
    void shouldProduceToString() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");
        pet.setBirthDate(new Date());
        PetType petType = new PetType();
        petType.setName("cat");
        pet.setType(petType);
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        pet.setOwner(owner);

        String result = pet.toString();
        assertThat(result).contains("Leo");
        assertThat(result).contains("cat");
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");

        assertThat(pet1).isEqualTo(pet2);
        assertThat(pet1.hashCode()).isEqualTo(pet2.hashCode());
    }

    @Test
    void shouldNotBeEqualToDifferentPet() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Milo");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        Pet pet = new Pet();
        pet.setId(1);
        assertThat(pet).isNotEqualTo("not a pet");
    }

    @Test
    void shouldBeEqualToSelf() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");
        assertThat(pet).isEqualTo(pet);
    }

    @Test
    void shouldNotBeEqualWhenNameDiffers() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Milo");

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotBeEqualWhenBirthDateDiffers() {
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(new Date(1000));

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");
        pet2.setBirthDate(new Date(2000));

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotBeEqualWhenTypeDiffers() {
        PetType cat = new PetType();
        cat.setName("cat");
        PetType dog = new PetType();
        dog.setName("dog");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setType(cat);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Leo");
        pet2.setType(dog);

        assertThat(pet1).isNotEqualTo(pet2);
    }

    @Test
    void shouldNotBeEqualWhenOwnerDiffers() {
        Owner owner1 = new Owner();
        owner1.setFirstName("George");
        Owner owner2 = new Owner();
        owner2.setFirstName("Betty");

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
