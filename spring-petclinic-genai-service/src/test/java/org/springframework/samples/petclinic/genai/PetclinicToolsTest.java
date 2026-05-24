package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.genai.dto.*;
import tools.jackson.core.JacksonException;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetclinicToolsTest {

    @Mock
    AIDataProvider aiDataProvider;

    @InjectMocks
    PetclinicTools petclinicTools;

    @Test
    void listOwnersCallsDataProvider() {
        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin", "addr", "city", "phone", List.of());
        when(aiDataProvider.getAllOwners()).thenReturn(List.of(owner));

        List<OwnerDetails> result = petclinicTools.listOwners();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("George");
        verify(aiDataProvider).getAllOwners();
    }

    @Test
    void addOwnerToPetclinicCallsDataProvider() {
        OwnerRequest request = new OwnerRequest("George", "Franklin", "addr", "city", "6085551023");
        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin", "addr", "city", "6085551023", List.of());
        when(aiDataProvider.addOwnerToPetclinic(request)).thenReturn(owner);

        OwnerDetails result = petclinicTools.addOwnerToPetclinic(request);

        assertThat(result.firstName()).isEqualTo("George");
        verify(aiDataProvider).addOwnerToPetclinic(request);
    }

    @Test
    void listVetsCallsDataProvider() throws JacksonException {
        Vet vetRequest = new Vet(1, "James", "Carter", Set.of());
        when(aiDataProvider.getVets(vetRequest)).thenReturn(List.of("vet info"));

        List<String> result = petclinicTools.listVets(vetRequest);

        assertThat(result).containsExactly("vet info");
        verify(aiDataProvider).getVets(vetRequest);
    }

    @Test
    void listVetsReturnsEmptyOnJacksonException() throws JacksonException {
        Vet vetRequest = new Vet(1, "James", "Carter", Set.of());
        when(aiDataProvider.getVets(vetRequest)).thenThrow(new TestJacksonException("test error"));

        List<String> result = petclinicTools.listVets(vetRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void addPetToOwnerCallsDataProvider() {
        PetRequest petRequest = new PetRequest(0, new Date(), "Buddy", 1);
        PetDetails petDetails = new PetDetails(1, "Buddy", "2020-01-01", new PetType("dog"), List.of());
        when(aiDataProvider.addPetToOwner(1, petRequest)).thenReturn(petDetails);

        PetDetails result = petclinicTools.addPetToOwner(1, petRequest);

        assertThat(result.name()).isEqualTo("Buddy");
        verify(aiDataProvider).addPetToOwner(1, petRequest);
    }

    static class TestJacksonException extends JacksonException {
        TestJacksonException(String msg) {
            super(msg);
        }
    }
}
