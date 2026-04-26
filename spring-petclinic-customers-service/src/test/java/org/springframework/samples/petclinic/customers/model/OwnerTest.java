package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerTest {

    @Test
    void shouldSetAndGetFields() {
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
    void shouldReturnNullIdByDefault() {
        Owner owner = new Owner();
        assertThat(owner.getId()).isNull();
    }

    @Test
    void shouldReturnEmptyPetsListWhenNoPets() {
        Owner owner = new Owner();
        assertThat(owner.getPets()).isEmpty();
    }

    @Test
    void shouldAddPetAndSetOwner() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Leo");
        owner.addPet(pet);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getName()).isEqualTo("Leo");
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnPetsSortedByName() {
        Owner owner = new Owner();
        Pet petB = new Pet();
        petB.setName("Basil");
        Pet petA = new Pet();
        petA.setName("Alpha");
        owner.addPet(petB);
        owner.addPet(petA);

        assertThat(owner.getPets()).extracting(Pet::getName).containsExactly("Alpha", "Basil");
    }

    @Test
    void shouldReturnToString() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        String toString = owner.toString();
        assertThat(toString).contains("George");
        assertThat(toString).contains("Franklin");
    }
}
