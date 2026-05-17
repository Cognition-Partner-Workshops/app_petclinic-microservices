package org.springframework.samples.petclinic.customers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OwnerRepository ownerRepository;

    @MockitoBean
    OwnerEntityMapper ownerEntityMapper;

    @Test
    void shouldCreateOwner() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890");

        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);

        String json = objectMapper.writeValueAsString(
            new OwnerRequest("John", "Doe", "123 Main", "Springfield", "1234567890"));

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldFindOwnerById() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("Jane");
        owner.setLastName("Smith");

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void shouldFindAllOwners() throws Exception {
        Owner o1 = new Owner();
        o1.setFirstName("A");
        o1.setLastName("B");
        Owner o2 = new Owner();
        o2.setFirstName("C");
        o2.setLastName("D");

        given(ownerRepository.findAll()).willReturn(List.of(o1, o2));

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("A"))
            .andExpect(jsonPath("$[1].firstName").value("C"));
    }

    @Test
    void shouldUpdateOwner() throws Exception {
        Owner existing = new Owner();
        existing.setFirstName("Old");
        existing.setLastName("Name");

        given(ownerRepository.findById(1)).willReturn(Optional.of(existing));
        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(existing);
        given(ownerRepository.save(any(Owner.class))).willReturn(existing);

        String json = objectMapper.writeValueAsString(
            new OwnerRequest("New", "Name", "addr", "city", "1111111111"));

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNoContent());

        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentOwner() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        String json = objectMapper.writeValueAsString(
            new OwnerRequest("New", "Name", "addr", "city", "1111111111"));

        mvc.perform(put("/owners/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }
}
