package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Provider-side contract verification test for the Customers Service.
 * Verifies the contract: GET /owners/{ownerId} returns owner details with pets.
 * This contract is consumed by the API Gateway's CustomersServiceClient.
 */
@WebMvcTest(OwnerResource.class)
@ActiveProfiles("test")
class ContractVerifierBaseTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @MockitoBean
    private OwnerEntityMapper ownerEntityMapper;

    @Test
    void contractShouldReturnOwnerById() throws Exception {
        PetType catType = new PetType();
        catType.setId(1);
        catType.setName("cat");

        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Leo");
        pet.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("2010-09-07"));
        pet.setType(catType);
        owner.addPet(pet);

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Franklin"))
            .andExpect(jsonPath("$.address").value("110 W. Liberty St."))
            .andExpect(jsonPath("$.city").value("Madison"))
            .andExpect(jsonPath("$.telephone").value("6085551023"))
            .andExpect(jsonPath("$.pets[0].id").value(1))
            .andExpect(jsonPath("$.pets[0].name").value("Leo"))
            .andExpect(jsonPath("$.pets[0].type.name").value("cat"));
    }
}
