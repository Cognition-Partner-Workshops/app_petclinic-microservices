package org.springframework.samples.petclinic.customers.web;

import java.util.List;
import java.util.Optional;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        Owner owner = createOwner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new OwnerRequest("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Franklin"));
    }

    @Test
    void shouldFindOwnerById() throws Exception {
        Owner owner = createOwner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Franklin"));
    }

    @Test
    void shouldReturnNullWhenOwnerNotFound() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(get("/owners/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllOwners() throws Exception {
        Owner owner1 = createOwner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        Owner owner2 = createOwner(2, "Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749");
        given(ownerRepository.findAll()).willReturn(List.of(owner1, owner2));

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("George"))
            .andExpect(jsonPath("$[1].firstName").value("Betty"));
    }

    @Test
    void shouldUpdateOwner() throws Exception {
        Owner owner = createOwner(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));
        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new OwnerRequest("George", "Franklin", "111 New St.", "Madison", "6085551023"))))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentOwner() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(put("/owners/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new OwnerRequest("George", "Franklin", "111 New St.", "Madison", "6085551023"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidOwnerRequest() throws Exception {
        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new OwnerRequest("", "", "", "", ""))))
            .andExpect(status().isBadRequest());
    }

    private Owner createOwner(int id, String firstName, String lastName, String address, String city, String telephone) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
        // id is auto-generated, but for test purposes we use reflection or rely on the returned value
        return owner;
    }
}
