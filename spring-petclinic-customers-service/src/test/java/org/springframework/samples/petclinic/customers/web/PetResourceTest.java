package org.springframework.samples.petclinic.customers.web;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Maciej Szarlinski
 */
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    PetRepository petRepository;

    @MockitoBean
    OwnerRepository ownerRepository;

    @Test
    void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        given(petRepository.findById(2)).willReturn(Optional.of(pet));


        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldReturnNotFoundForUnknownPet() throws Exception {
        given(petRepository.findById(404)).willReturn(Optional.empty());

        mvc.perform(get("/owners/2/pets/404").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPetTypes() throws Exception {
        PetType type = new PetType();
        type.setId(1);
        type.setName("cat");
        given(petRepository.findPetTypes()).willReturn(List.of(type));

        mvc.perform(get("/petTypes").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("cat"));
    }

    @Test
    void shouldCreatePetForOwner() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        PetType type = new PetType();
        type.setId(6);
        type.setName("hamster");

        given(ownerRepository.findById(2)).willReturn(Optional.of(owner));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.of(type));
        given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post("/owners/2/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":0,\"name\":\"Basil\",\"birthDate\":\"2020-01-01\",\"typeId\":6}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingPetForUnknownOwner() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(post("/owners/999/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":0,\"name\":\"Basil\",\"birthDate\":\"2020-01-01\",\"typeId\":6}"))
            .andExpect(status().isNotFound());

        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void shouldUpdatePet() throws Exception {
        Pet pet = setupPet();
        given(petRepository.findById(2)).willReturn(Optional.of(pet));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.empty());
        given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(put("/owners/2/pets/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":2,\"name\":\"Rex\",\"birthDate\":\"2020-01-01\",\"typeId\":6}"))
            .andExpect(status().isNoContent());

        verify(petRepository).save(pet);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPet() throws Exception {
        given(petRepository.findById(404)).willReturn(Optional.empty());

        mvc.perform(put("/owners/2/pets/404")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":404,\"name\":\"Rex\",\"birthDate\":\"2020-01-01\",\"typeId\":6}"))
            .andExpect(status().isNotFound());

        verify(petRepository, never()).save(any(Pet.class));
    }

    private Pet setupPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }
}
