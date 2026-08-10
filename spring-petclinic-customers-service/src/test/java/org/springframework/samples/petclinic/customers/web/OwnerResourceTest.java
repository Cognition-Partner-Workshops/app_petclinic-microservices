package org.springframework.samples.petclinic.customers.web;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerResource.class)
@Import(OwnerEntityMapper.class)
@ActiveProfiles("test")
class OwnerResourceTest {

    private static final String VALID_OWNER_JSON = """
        {"firstName":"George","lastName":"Bush","address":"Rue de la Paix","city":"Paris","telephone":"0123456789"}
        """;

    @Autowired
    MockMvc mvc;

    @MockitoBean
    OwnerRepository ownerRepository;

    @Test
    void shouldCreateOwner() throws Exception {
        given(ownerRepository.save(any(Owner.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_OWNER_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Bush"))
            .andExpect(jsonPath("$.city").value("Paris"));
    }

    @Test
    void shouldRejectOwnerWithBlankFirstName() throws Exception {
        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"","lastName":"Bush","address":"Rue de la Paix","city":"Paris","telephone":"0123456789"}
                    """))
            .andExpect(status().isBadRequest());

        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldRejectOwnerWithNonNumericTelephone() throws Exception {
        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"George","lastName":"Bush","address":"Rue de la Paix","city":"Paris","telephone":"not-a-phone"}
                    """))
            .andExpect(status().isBadRequest());

        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldRejectMalformedRequestBody() throws Exception {
        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ not json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindSingleOwner() throws Exception {
        given(ownerRepository.findById(1)).willReturn(Optional.of(setupOwner()));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("George"));
    }

    @Test
    void shouldReturnEmptyBodyWhenOwnerDoesNotExist() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(get("/owners/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void shouldFindAllOwners() throws Exception {
        given(ownerRepository.findAll()).willReturn(List.of(setupOwner()));

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Bush"));
    }

    @Test
    void shouldUpdateOwner() throws Exception {
        Owner owner = setupOwner();
        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_OWNER_JSON))
            .andExpect(status().isNoContent());

        verify(ownerRepository).save(owner);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownOwner() throws Exception {
        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(put("/owners/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_OWNER_JSON))
            .andExpect(status().isNotFound());

        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldRejectUpdateWithInvalidPayload() throws Exception {
        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"firstName":"George","lastName":"","address":"Rue de la Paix","city":"Paris","telephone":"0123456789"}
                    """))
            .andExpect(status().isBadRequest());

        verify(ownerRepository, never()).save(any(Owner.class));
    }

    private Owner setupOwner() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("Rue de la Paix");
        owner.setCity("Paris");
        owner.setTelephone("0123456789");
        return owner;
    }
}
