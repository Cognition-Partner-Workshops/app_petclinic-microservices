package org.springframework.samples.petclinic.customers.model;

import java.util.Date;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OwnerTest {

    @Test
    void shouldExposePetsSortedByNameAndSetBackReference() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet zeus = pet(1, "Zeus");
        Pet basil = pet(2, "Basil");
        owner.addPet(zeus);
        owner.addPet(basil);

        assertThat(owner.getPets()).extracting(Pet::getName).containsExactly("Basil", "Zeus");
        assertThat(zeus.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnUnmodifiablePetList() {
        Owner owner = new Owner();

        assertThatThrownBy(() -> owner.getPets().add(pet(1, "Basil")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldExposeAllAttributesInToString() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("Rue de la Paix");
        owner.setCity("Paris");
        owner.setTelephone("0123456789");

        assertThat(owner.getId()).isNull();
        assertThat(owner.toString())
            .contains("George")
            .contains("Bush")
            .contains("Rue de la Paix")
            .contains("Paris")
            .contains("0123456789");
    }

    @Test
    void shouldDescribePetIncludingOwnerAndType() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        PetType type = new PetType();
        type.setId(6);
        type.setName("hamster");

        Pet pet = pet(1, "Basil");
        pet.setType(type);
        pet.setBirthDate(new Date(0));
        owner.addPet(pet);

        assertThat(pet.toString()).contains("Basil").contains("hamster").contains("George");
        assertThat(pet.getBirthDate()).isEqualTo(new Date(0));
        assertThat(type.getId()).isEqualTo(6);
        assertThat(type.getName()).isEqualTo("hamster");
    }

    @Test
    void petsWithSameStateShouldBeEqual() {
        Pet one = pet(1, "Basil");
        Pet other = pet(1, "Basil");

        assertThat(one).isEqualTo(other).hasSameHashCodeAs(other);
        assertThat(one).isNotEqualTo(pet(2, "Basil"));
        assertThat(one).isNotEqualTo(null);
        assertThat(one).isNotEqualTo("not a pet");
    }

    private Pet pet(int id, String name) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        return pet;
    }
}
