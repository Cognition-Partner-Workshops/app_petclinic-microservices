package org.springframework.samples.petclinic.jhipster.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.jhipster.model.BankAccount;
import org.springframework.samples.petclinic.jhipster.model.BankAccountRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the BankAccountResource REST controller.
 */
@WebMvcTest(BankAccountResource.class)
@ActiveProfiles("test")
class BankAccountResourceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BankAccountRepository bankAccountRepository;

    @Test
    @WithMockUser
    void shouldGetBankAccountInJsonFormat() throws Exception {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setName("Test Account");
        bankAccount.setBalance(new BigDecimal("1000.00"));

        given(bankAccountRepository.findOneWithEagerRelationships(1L)).willReturn(Optional.of(bankAccount));

        mvc.perform(get("/api/bank-accounts/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Account"))
            .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    @WithMockUser
    void shouldReturnAllBankAccounts() throws Exception {
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setId(1L);
        bankAccount1.setName("Account 1");
        bankAccount1.setBalance(new BigDecimal("1000.00"));

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setId(2L);
        bankAccount2.setName("Account 2");
        bankAccount2.setBalance(new BigDecimal("2500.50"));

        given(bankAccountRepository.findAllWithEagerRelationships()).willReturn(List.of(bankAccount1, bankAccount2));

        mvc.perform(get("/api/bank-accounts").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Account 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Account 2"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForNonExistentBankAccount() throws Exception {
        given(bankAccountRepository.findOneWithEagerRelationships(999L)).willReturn(Optional.empty());

        mvc.perform(get("/api/bank-accounts/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
