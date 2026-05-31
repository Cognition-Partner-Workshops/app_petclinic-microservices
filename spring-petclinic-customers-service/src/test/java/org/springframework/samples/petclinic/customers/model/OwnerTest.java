package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerTest {

    @Test
    void shouldGetPetsReturnEmptyListWhenNoPets() {
        Owner owner = new Owner();
        assertThat(owner.getPets()).isEmpty();
    }

    @Test
    void shouldAddPetAndSetOwner() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Fido");

        owner.addPet(pet);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getName()).isEqualTo("Fido");
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnPetsSortedByName() {
        Owner owner = new Owner();
        Pet pet1 = new Pet();
        pet1.setName("Zebra");
        Pet pet2 = new Pet();
        pet2.setName("Alpha");

        owner.addPet(pet1);
        owner.addPet(pet2);

        assertThat(owner.getPets()).extracting(Pet::getName).containsExactly("Alpha", "Zebra");
    }

    @Test
    void shouldToStringContainOwnerDetails() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        String result = owner.toString();

        assertThat(result).contains("George");
        assertThat(result).contains("Franklin");
        assertThat(result).contains("Madison");
    }

    @Test
    void shouldSetAndGetFields() {
        Owner owner = new Owner();
        owner.setFirstName("Betty");
        owner.setLastName("Davis");
        owner.setAddress("638 Cardinal Ave.");
        owner.setCity("Sun Prairie");
        owner.setTelephone("6085551749");

        assertThat(owner.getFirstName()).isEqualTo("Betty");
        assertThat(owner.getLastName()).isEqualTo("Davis");
        assertThat(owner.getAddress()).isEqualTo("638 Cardinal Ave.");
        assertThat(owner.getCity()).isEqualTo("Sun Prairie");
        assertThat(owner.getTelephone()).isEqualTo("6085551749");
    }
}
