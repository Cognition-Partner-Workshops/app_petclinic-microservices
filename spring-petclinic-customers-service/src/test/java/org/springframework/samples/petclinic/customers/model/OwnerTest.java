package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerTest {

    @Test
    void shouldGetAndSetFields() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        assertThat(owner.getFirstName()).isEqualTo("George");
        assertThat(owner.getLastName()).isEqualTo("Franklin");
        assertThat(owner.getAddress()).isEqualTo("110 W. Liberty St.");
        assertThat(owner.getCity()).isEqualTo("Madison");
        assertThat(owner.getTelephone()).isEqualTo("6085551023");
    }

    @Test
    void shouldReturnEmptyPetsListWhenNoPetsAdded() {
        Owner owner = new Owner();
        assertThat(owner.getPets()).isEmpty();
    }

    @Test
    void shouldAddPetAndSetOwnerBackReference() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Basil");

        owner.addPet(pet);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getName()).isEqualTo("Basil");
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnSortedPetsByName() {
        Owner owner = new Owner();
        Pet petC = new Pet();
        petC.setName("Charlie");
        Pet petA = new Pet();
        petA.setName("Alpha");
        Pet petB = new Pet();
        petB.setName("Bravo");

        owner.addPet(petC);
        owner.addPet(petA);
        owner.addPet(petB);

        assertThat(owner.getPets()).extracting(Pet::getName)
            .containsExactly("Alpha", "Bravo", "Charlie");
    }

    @Test
    void shouldReturnIdAsNull() {
        Owner owner = new Owner();
        assertThat(owner.getId()).isNull();
    }

    @Test
    void shouldProduceToString() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        String result = owner.toString();
        assertThat(result).contains("lastName", "Franklin");
        assertThat(result).contains("firstName", "George");
        assertThat(result).contains("address", "110 W. Liberty St.");
        assertThat(result).contains("city", "Madison");
        assertThat(result).contains("telephone", "6085551023");
    }
}
