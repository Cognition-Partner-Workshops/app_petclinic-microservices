package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void shouldCreateFromPetEntity() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        PetType type = new PetType();
        type.setId(1);
        type.setName("cat");

        Pet pet = new Pet();
        pet.setId(10);
        pet.setName("Leo");
        pet.setBirthDate(new Date());
        pet.setType(type);
        pet.setOwner(owner);

        PetDetails details = new PetDetails(pet);

        assertThat(details.id()).isEqualTo(10);
        assertThat(details.name()).isEqualTo("Leo");
        assertThat(details.owner()).isEqualTo("George Franklin");
        assertThat(details.type()).isEqualTo(type);
        assertThat(details.birthDate()).isNotNull();
    }
}
