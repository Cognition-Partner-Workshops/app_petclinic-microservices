package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void constructsFromPetEntity() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        PetType type = new PetType();
        type.setId(1);
        type.setName("cat");

        Date birthDate = new Date();
        Pet pet = new Pet();
        pet.setId(5);
        pet.setName("Leo");
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setOwner(owner);

        PetDetails details = new PetDetails(pet);

        assertThat(details.id()).isEqualTo(5);
        assertThat(details.name()).isEqualTo("Leo");
        assertThat(details.owner()).isEqualTo("George Franklin");
        assertThat(details.birthDate()).isEqualTo(birthDate);
        assertThat(details.type()).isEqualTo(type);
    }
}
