package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerResource.class)
@ActiveProfiles("test")
class OwnerResourceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    OwnerRepository ownerRepository;

    @MockitoBean
    OwnerEntityMapper ownerEntityMapper;

    @Test
    void shouldCreateOwner() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");

        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"John","lastName":"Doe","address":"123 Main","city":"NYC","telephone":"1234567890"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void shouldFindOwnerById() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("Jane");
        owner.setLastName("Doe");

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void shouldReturnEmptyWhenOwnerNotFound() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(get("/owners/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllOwners() throws Exception {
        Owner owner1 = new Owner();
        owner1.setFirstName("John");
        Owner owner2 = new Owner();
        owner2.setFirstName("Jane");

        given(ownerRepository.findAll()).willReturn(List.of(owner1, owner2));

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void shouldUpdateOwner() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("John");

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));
        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"John","lastName":"Updated","address":"456 Elm","city":"LA","telephone":"0987654321"}
                    """))
            .andExpect(status().isNoContent());

        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentOwner() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(put("/owners/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"John","lastName":"Doe","address":"123 Main","city":"NYC","telephone":"1234567890"}
                    """))
            .andExpect(status().isNotFound());
    }
}
