package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerTest {

    @Test
    void gettersAndSetters() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890");

        assertThat(owner.getFirstName()).isEqualTo("John");
        assertThat(owner.getLastName()).isEqualTo("Doe");
        assertThat(owner.getAddress()).isEqualTo("123 Main St");
        assertThat(owner.getCity()).isEqualTo("Springfield");
        assertThat(owner.getTelephone()).isEqualTo("1234567890");
    }

    @Test
    void idIsNullByDefault() {
        Owner owner = new Owner();
        assertThat(owner.getId()).isNull();
    }

    @Test
    void getPetsReturnsEmptyListWhenNoPets() {
        Owner owner = new Owner();
        List<Pet> pets = owner.getPets();
        assertThat(pets).isEmpty();
    }

    @Test
    void addPetSetsBidirectionalRelationship() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Buddy");

        owner.addPet(pet);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getName()).isEqualTo("Buddy");
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void getPetsReturnsSortedByName() {
        Owner owner = new Owner();

        Pet zebra = new Pet();
        zebra.setName("Zebra");
        owner.addPet(zebra);

        Pet alpha = new Pet();
        alpha.setName("Alpha");
        owner.addPet(alpha);

        List<Pet> pets = owner.getPets();
        assertThat(pets).hasSize(2);
        assertThat(pets.get(0).getName()).isEqualTo("Alpha");
        assertThat(pets.get(1).getName()).isEqualTo("Zebra");
    }

    @Test
    void getPetsInternalInitializesSetOnSecondCall() {
        Owner owner = new Owner();
        // First call initializes the set
        owner.getPets();
        // Second call reuses the existing set (covers else branch)
        List<Pet> pets = owner.getPets();
        assertThat(pets).isEmpty();
    }

    @Test
    void toStringContainsOwnerFields() {
        Owner owner = new Owner();
        owner.setFirstName("Jane");
        owner.setLastName("Smith");
        owner.setAddress("456 Oak Ave");
        owner.setCity("Portland");
        owner.setTelephone("9876543210");

        String str = owner.toString();
        assertThat(str).contains("Jane");
        assertThat(str).contains("Smith");
        assertThat(str).contains("456 Oak Ave");
        assertThat(str).contains("Portland");
        assertThat(str).contains("9876543210");
    }
}
