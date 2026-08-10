package org.springframework.samples.petclinic.customers.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryIntegrationTest {

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    PetRepository petRepository;

    @Test
    void shouldFindOwnerLoadedFromDataScript() {
        Optional<Owner> owner = ownerRepository.findById(1);

        assertThat(owner).isPresent();
        assertThat(owner.get().getLastName()).isEqualTo("Franklin");
        assertThat(owner.get().getPets()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownOwner() {
        assertThat(ownerRepository.findById(9999)).isEmpty();
    }

    @Test
    void shouldPersistNewOwner() {
        Owner owner = new Owner();
        owner.setFirstName("Ada");
        owner.setLastName("Lovelace");
        owner.setAddress("1 Analytical Engine Way");
        owner.setCity("London");
        owner.setTelephone("0123456789");

        Owner saved = ownerRepository.save(owner);

        assertThat(saved.getId()).isNotNull();
        assertThat(ownerRepository.findById(saved.getId())).contains(saved);
    }

    @Test
    void shouldFindPetTypesOrderedByName() {
        List<PetType> types = petRepository.findPetTypes();

        assertThat(types).extracting(PetType::getName).containsExactly(
            "bird", "cat", "dog", "hamster", "lizard", "snake");
    }

    @Test
    void shouldFindPetTypeById() {
        assertThat(petRepository.findPetTypeById(1)).isPresent();
        assertThat(petRepository.findPetTypeById(9999)).isEmpty();
    }

    @Test
    void shouldPersistNewPetForExistingOwner() {
        Owner owner = ownerRepository.findById(1).orElseThrow();
        Pet pet = new Pet();
        pet.setName("Rex");
        pet.setBirthDate(new Date());
        pet.setType(petRepository.findPetTypeById(2).orElseThrow());
        owner.addPet(pet);

        Pet saved = petRepository.save(pet);

        assertThat(saved.getId()).isNotNull();
        assertThat(petRepository.findById(saved.getId()))
            .get()
            .extracting(Pet::getName)
            .isEqualTo("Rex");
    }

    @Test
    void shouldReturnEmptyForUnknownPet() {
        assertThat(petRepository.findById(9999)).isEmpty();
    }
}
