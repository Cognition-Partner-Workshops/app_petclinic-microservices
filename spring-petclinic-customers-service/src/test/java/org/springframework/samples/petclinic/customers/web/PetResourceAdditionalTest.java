package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceAdditionalTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    PetRepository petRepository;

    @MockitoBean
    OwnerRepository ownerRepository;

    @Test
    void shouldGetPetTypes() throws Exception {
        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");
        PetType dog = new PetType();
        dog.setId(2);
        dog.setName("dog");

        given(petRepository.findPetTypes()).willReturn(List.of(cat, dog));

        mvc.perform(get("/petTypes").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("cat"))
            .andExpect(jsonPath("$[1].name").value("dog"));
    }

    @Test
    void shouldCreatePet() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        PetType type = new PetType();
        type.setId(6);
        type.setName("hamster");

        Pet savedPet = new Pet();
        savedPet.setId(1);
        savedPet.setName("Basil");
        savedPet.setType(type);
        savedPet.setOwner(owner);

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.of(type));
        given(petRepository.save(any(Pet.class))).willReturn(savedPet);

        mvc.perform(post("/owners/1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":0,"birthDate":"2020-01-01","name":"Basil","typeId":6}
                    """))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn404WhenCreatingPetForNonExistentOwner() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(post("/owners/999/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":0,"birthDate":"2020-01-01","name":"Basil","typeId":6}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdatePet() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        PetType type = new PetType();
        type.setId(6);

        Pet pet = new Pet();
        pet.setId(2);
        pet.setName("Basil");
        pet.setType(type);
        pet.setOwner(owner);

        given(petRepository.findById(2)).willReturn(Optional.of(pet));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.of(type));
        given(petRepository.save(any(Pet.class))).willReturn(pet);

        mvc.perform(put("/owners/1/pets/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":2,"birthDate":"2020-01-01","name":"Basil Updated","typeId":6}
                    """))
            .andExpect(status().isNoContent());

        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentPet() throws Exception {
        given(petRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(put("/owners/1/pets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":999,"birthDate":"2020-01-01","name":"Ghost","typeId":6}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenGettingNonExistentPet() throws Exception {
        given(petRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(get("/owners/1/pets/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
