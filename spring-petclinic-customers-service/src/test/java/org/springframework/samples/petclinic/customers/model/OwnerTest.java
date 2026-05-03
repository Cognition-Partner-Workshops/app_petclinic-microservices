package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.Test;

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
        assertThat(owner.getId()).isNull();
    }

    @Test
    void getPetsReturnsEmptyListWhenNoPets() {
        Owner owner = new Owner();
        assertThat(owner.getPets()).isEmpty();
    }

    @Test
    void addPetSetsOwnerAndAddsToPets() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Fido");

        owner.addPet(pet);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getName()).isEqualTo("Fido");
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void getPetsReturnsSortedByName() {
        Owner owner = new Owner();

        Pet petB = new Pet();
        petB.setName("Buddy");
        owner.addPet(petB);

        Pet petA = new Pet();
        petA.setName("Alpha");
        owner.addPet(petA);

        assertThat(owner.getPets()).extracting(Pet::getName)
            .containsExactly("Alpha", "Buddy");
    }

    @Test
    void getPetsReturnsUnmodifiableList() {
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Rex");
        owner.addPet(pet);

        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> owner.getPets().add(new Pet())
        );
    }

    @Test
    void toStringContainsFields() {
        Owner owner = new Owner();
        owner.setFirstName("Jane");
        owner.setLastName("Smith");
        owner.setAddress("456 Elm St");
        owner.setCity("Portland");
        owner.setTelephone("9876543210");

        String result = owner.toString();
        assertThat(result).contains("lastName", "Smith");
        assertThat(result).contains("firstName", "Jane");
        assertThat(result).contains("456 Elm St");
        assertThat(result).contains("Portland");
        assertThat(result).contains("9876543210");
    }
}
