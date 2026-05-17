package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void shouldConstructFromPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        PetType type = new PetType();
        type.setId(6);
        type.setName("Cat");

        Date birthDate = new Date();

        Pet pet = new Pet();
        pet.setId(2);
        pet.setName("Basil");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        PetDetails details = new PetDetails(pet);

        assertThat(details.id()).isEqualTo(2);
        assertThat(details.name()).isEqualTo("Basil");
        assertThat(details.owner()).isEqualTo("George Bush");
        assertThat(details.birthDate()).isEqualTo(birthDate);
        assertThat(details.type()).isSameAs(type);
    }
}
