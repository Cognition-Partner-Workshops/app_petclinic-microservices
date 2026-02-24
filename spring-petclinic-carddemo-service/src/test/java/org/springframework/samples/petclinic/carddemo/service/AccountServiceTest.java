package org.springframework.samples.petclinic.carddemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.carddemo.model.Account;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comparison tests verifying that the Java AccountService produces results
 * matching the expected COBOL logic from CBTRN02C and CBACT04C.
 *
 * <p>Uses sample data from app/data/ASCII/ loaded via data.sql.
 */
@SpringBootTest
@Sql(scripts = {"/schema.sql", "/data.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("Account data loads correctly from sample acctdata.txt")
    void shouldLoadAccountDataFromSample() {
        // Verify account 1 from acctdata.txt:
        // 00000000001Y00000001940{00000020200{00000010200{...
        // bal=194.00, limit=2020.00, cash_limit=1020.00
        Account acct = accountService.findById(1L).orElseThrow();
        assertThat(acct.getActiveStatus()).isEqualTo("Y");
        assertThat(acct.getCurrentBalance()).isEqualByComparingTo("194.00");
        assertThat(acct.getCreditLimit()).isEqualByComparingTo("2020.00");
        assertThat(acct.getCashCreditLimit()).isEqualByComparingTo("1020.00");
        assertThat(acct.getOpenDate()).isEqualTo(LocalDate.of(2014, 11, 20));
        assertThat(acct.getExpirationDate()).isEqualTo(LocalDate.of(2025, 5, 20));
        assertThat(acct.getGroupId()).isEqualTo("A000000000");
    }

    @Test
    @DisplayName("CBTRN02C 2800: positive transaction adds to balance and cycle credit")
    void shouldApplyPositiveTransactionToBalance() {
        // Mirrors CBTRN02C paragraph 2800-UPDATE-ACCOUNT-REC:
        //   ADD DALYTRAN-AMT TO ACCT-CURR-BAL
        //   IF DALYTRAN-AMT >= 0
        //     ADD DALYTRAN-AMT TO ACCT-CURR-CYC-CREDIT
        Account account = new Account(
            999L, "Y", new BigDecimal("1000.00"), new BigDecimal("5000.00"),
            new BigDecimal("2500.00"), LocalDate.now(), LocalDate.now().plusYears(2),
            LocalDate.now(), new BigDecimal("200.00"), new BigDecimal("-100.00"),
            "A000000000"
        );
        account = accountService.save(account);

        BigDecimal txnAmount = new BigDecimal("350.50");
        accountService.applyTransactionToBalance(account, txnAmount);

        Account updated = accountService.findById(999L).orElseThrow();
        // COBOL: 1000.00 + 350.50 = 1350.50
        assertThat(updated.getCurrentBalance()).isEqualByComparingTo("1350.50");
        // COBOL: 200.00 + 350.50 = 550.50 (positive -> cycle credit)
        assertThat(updated.getCurrentCycleCredit()).isEqualByComparingTo("550.50");
        // Debit unchanged
        assertThat(updated.getCurrentCycleDebit()).isEqualByComparingTo("-100.00");
    }

    @Test
    @DisplayName("CBTRN02C 2800: negative transaction adds to balance and cycle debit")
    void shouldApplyNegativeTransactionToBalance() {
        // Mirrors CBTRN02C paragraph 2800-UPDATE-ACCOUNT-REC:
        //   ADD DALYTRAN-AMT TO ACCT-CURR-BAL
        //   ELSE (negative)
        //     ADD DALYTRAN-AMT TO ACCT-CURR-CYC-DEBIT
        Account account = new Account(
            998L, "Y", new BigDecimal("2000.00"), new BigDecimal("8000.00"),
            new BigDecimal("4000.00"), LocalDate.now(), LocalDate.now().plusYears(2),
            LocalDate.now(), new BigDecimal("500.00"), new BigDecimal("-200.00"),
            "A000000000"
        );
        account = accountService.save(account);

        BigDecimal txnAmount = new BigDecimal("-175.25");
        accountService.applyTransactionToBalance(account, txnAmount);

        Account updated = accountService.findById(998L).orElseThrow();
        // COBOL: 2000.00 + (-175.25) = 1824.75
        assertThat(updated.getCurrentBalance()).isEqualByComparingTo("1824.75");
        // Credit unchanged
        assertThat(updated.getCurrentCycleCredit()).isEqualByComparingTo("500.00");
        // COBOL: -200.00 + (-175.25) = -375.25 (negative -> cycle debit)
        assertThat(updated.getCurrentCycleDebit()).isEqualByComparingTo("-375.25");
    }

    @Test
    @DisplayName("CBACT04C 1050: interest applied and cycle accumulators reset")
    void shouldApplyInterestAndResetCycle() {
        // Mirrors CBACT04C paragraph 1050-UPDATE-ACCOUNT:
        //   ADD WS-TOTAL-INT TO ACCT-CURR-BAL
        //   MOVE 0 TO ACCT-CURR-CYC-CREDIT
        //   MOVE 0 TO ACCT-CURR-CYC-DEBIT
        Account account = new Account(
            997L, "Y", new BigDecimal("5000.00"), new BigDecimal("10000.00"),
            new BigDecimal("5000.00"), LocalDate.now(), LocalDate.now().plusYears(2),
            LocalDate.now(), new BigDecimal("800.00"), new BigDecimal("-300.00"),
            "A000000000"
        );
        account = accountService.save(account);

        BigDecimal totalInterest = new BigDecimal("62.50");
        accountService.applyInterestAndResetCycle(account, totalInterest);

        Account updated = accountService.findById(997L).orElseThrow();
        // COBOL: 5000.00 + 62.50 = 5062.50
        assertThat(updated.getCurrentBalance()).isEqualByComparingTo("5062.50");
        // COBOL: MOVE 0 TO ACCT-CURR-CYC-CREDIT
        assertThat(updated.getCurrentCycleCredit()).isEqualByComparingTo("0.00");
        // COBOL: MOVE 0 TO ACCT-CURR-CYC-DEBIT
        assertThat(updated.getCurrentCycleDebit()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("CBTRN02C 1500-B: credit limit check passes for within-limit transaction")
    void shouldPassCreditLimitCheck() {
        // Mirrors CBTRN02C paragraph 1500-B-LOOKUP-ACCT:
        //   COMPUTE WS-TEMP-BAL = ACCT-CURR-CYC-CREDIT - ACCT-CURR-CYC-DEBIT + DALYTRAN-AMT
        //   IF ACCT-CREDIT-LIMIT >= WS-TEMP-BAL -> OK
        //
        // Account 27: credit_limit=10000, cyc_credit=100, cyc_debit=-50
        // temp_bal = 100 - (-50) + 500 = 650
        // 10000 >= 650 -> true
        Account account = accountService.findById(27L).orElseThrow();
        boolean result = accountService.isWithinCreditLimit(account, new BigDecimal("500.00"));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("CBTRN02C 1500-B: credit limit check fails for over-limit transaction")
    void shouldFailCreditLimitCheck() {
        // Account 1: credit_limit=2020, cyc_credit=0, cyc_debit=0
        // temp_bal = 0 - 0 + 3000 = 3000
        // 2020 >= 3000 -> false
        Account account = accountService.findById(1L).orElseThrow();
        boolean result = accountService.isWithinCreditLimit(account, new BigDecimal("3000.00"));
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("CBTRN02C 1500-B: credit limit check with negative debit (COBOL signed arithmetic)")
    void shouldHandleSignedArithmeticInCreditLimitCheck() {
        // Account 50: credit_limit=5000, cyc_credit=200, cyc_debit=-100
        // COBOL: WS-TEMP-BAL = 200 - (-100) + 4500 = 4800
        // 5000 >= 4800 -> true (just within limit)
        Account account = accountService.findById(50L).orElseThrow();
        boolean withinLimit = accountService.isWithinCreditLimit(account, new BigDecimal("4500.00"));
        assertThat(withinLimit).isTrue();

        // Now 5000 >= 200 - (-100) + 5000 = 5300 -> false
        boolean overLimit = accountService.isWithinCreditLimit(account, new BigDecimal("5000.00"));
        assertThat(overLimit).isFalse();
    }
}
