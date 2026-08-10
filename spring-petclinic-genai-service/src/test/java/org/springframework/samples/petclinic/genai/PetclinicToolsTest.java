package org.springframework.samples.petclinic.genai;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.PetType;
import org.springframework.samples.petclinic.genai.dto.Vet;
import tools.jackson.core.JacksonException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetclinicToolsTest {

    @Mock
    AIDataProvider dataProvider;

    @InjectMocks
    PetclinicTools tools;

    @Test
    void shouldListOwners() {
        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin", "110 W. Liberty St.",
            "Madison", "6085551023", List.of());
        given(dataProvider.getAllOwners()).willReturn(List.of(owner));

        assertThat(tools.listOwners()).containsExactly(owner);
    }

    @Test
    void shouldAddOwner() {
        OwnerRequest request = new OwnerRequest("Ada", "Lovelace", "1 Analytical Way", "London", "0123456789");
        OwnerDetails created = new OwnerDetails(2, "Ada", "Lovelace", "1 Analytical Way",
            "London", "0123456789", List.of());
        given(dataProvider.addOwnerToPetclinic(request)).willReturn(created);

        assertThat(tools.addOwnerToPetclinic(request)).isEqualTo(created);
    }

    @Test
    void shouldListVets() throws JacksonException {
        Vet vet = new Vet(1, "James", "Carter", null);
        given(dataProvider.getVets(vet)).willReturn(List.of("James Carter"));

        assertThat(tools.listVets(vet)).containsExactly("James Carter");
    }

    @Test
    void shouldListAllVetsWhenNoCriteriaGiven() throws JacksonException {
        given(dataProvider.getVets(null)).willReturn(List.of("James Carter", "Helen Leary"));

        assertThat(tools.listVets(null)).hasSize(2);
    }

    @Test
    void shouldReturnEmptyVetListWhenSerializationFails() throws JacksonException {
        given(dataProvider.getVets(any())).willThrow(new JacksonException("boom") {
        });

        assertThat(tools.listVets(new Vet(1, "James", "Carter", null))).isEmpty();
    }

    @Test
    void shouldAddPetToOwner() {
        PetRequest request = new PetRequest(0, new Date(0), "Leo", 1);
        PetDetails created = new PetDetails(20, "Leo", "2010-09-07", new PetType("cat"), List.of());
        given(dataProvider.addPetToOwner(1, request)).willReturn(created);

        assertThat(tools.addPetToOwner(1, request)).isEqualTo(created);
        verify(dataProvider).addPetToOwner(1, request);
    }
}
