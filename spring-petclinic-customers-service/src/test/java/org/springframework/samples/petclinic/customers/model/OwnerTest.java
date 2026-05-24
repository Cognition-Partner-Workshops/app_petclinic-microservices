package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerTest {

    @Test
    void getPetsReturnsEmptyListWhenNoPets() {
        Owner owner = new Owner();
        List<Pet> pets = owner.getPets();
        assertThat(pets).isEmpty();
    }

    @Test
    void addPetSetsOwnerOnPet() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Buddy");
        owner.addPet(pet);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void getPetsReturnsSortedByName() {
        Owner owner = new Owner();

        Pet petZ = new Pet();
        petZ.setName("Ziggy");
        owner.addPet(petZ);

        Pet petA = new Pet();
        petA.setName("Alpha");
        owner.addPet(petA);

        Pet petM = new Pet();
        petM.setName("Milo");
        owner.addPet(petM);

        List<Pet> pets = owner.getPets();
        assertThat(pets).extracting(Pet::getName)
            .containsExactly("Alpha", "Milo", "Ziggy");
    }

    @Test
    void getPetsReturnsUnmodifiableList() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Buddy");
        owner.addPet(pet);

        List<Pet> pets = owner.getPets();
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class, () -> pets.add(new Pet()));
    }

    @Test
    void gettersAndSetters() {
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
    void toStringContainsAllFields() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        String str = owner.toString();
        assertThat(str).contains("George");
        assertThat(str).contains("Franklin");
        assertThat(str).contains("110 W. Liberty St.");
        assertThat(str).contains("Madison");
        assertThat(str).contains("6085551023");
    }

    @Test
    void getIdReturnsNullWhenNotSet() {
        Owner owner = new Owner();
        assertThat(owner.getId()).isNull();
    }
}
