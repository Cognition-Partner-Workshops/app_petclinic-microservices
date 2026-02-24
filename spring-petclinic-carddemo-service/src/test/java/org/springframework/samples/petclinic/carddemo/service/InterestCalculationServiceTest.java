package org.springframework.samples.petclinic.carddemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.carddemo.model.Account;
import org.springframework.samples.petclinic.carddemo.repository.TransactionRepository;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comparison tests verifying that the Java InterestCalculationService produces
 * results matching the expected COBOL logic from CBACT04C.
 *
 * <p>The core COBOL formula (paragraph 1300-COMPUTE-INTEREST):
 * <pre>
 *   COMPUTE WS-MONTHLY-INT = (TRAN-CAT-BAL * DIS-INT-RATE) / 1200
 * </pre>
 *
 * <p>Uses sample data from app/data/ASCII/ loaded via data.sql.
 */
@SpringBootTest
@Sql(scripts = {"/schema.sql", "/data.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InterestCalculationServiceTest {

    @Autowired
    private InterestCalculationService interestCalculationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    // -----------------------------------------------------------------------
    // Unit-level formula tests (CBACT04C paragraph 1300-COMPUTE-INTEREST)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CBACT04C 1300: monthly interest = (balance * rate) / 1200")
    void shouldComputeMonthlyInterestMatchingCobolFormula() {
        // COBOL: COMPUTE WS-MONTHLY-INT = (TRAN-CAT-BAL * DIS-INT-RATE) / 1200
        //
        // Example: balance=1200.00, rate=15.00
        // Expected: (1200.00 * 15.00) / 1200 = 18000.00 / 1200 = 15.00
        BigDecimal result = interestCalculationService.computeMonthlyInterest(
            new BigDecimal("1200.00"), new BigDecimal("15.00")
        );
        assertThat(result).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("CBACT04C 1300: monthly interest with fractional result rounds correctly")
    void shouldRoundInterestToTwoDecimalPlaces() {
        // balance=500.00, rate=25.00
        // (500.00 * 25.00) / 1200 = 12500.00 / 1200 = 10.416666...
        // COBOL S9(09)V99 truncates/rounds to 2 decimal places
        BigDecimal result = interestCalculationService.computeMonthlyInterest(
            new BigDecimal("500.00"), new BigDecimal("25.00")
        );
        // Expected: 10.42 (rounded half-up)
        assertThat(result).isEqualByComparingTo("10.42");
    }

    @Test
    @DisplayName("CBACT04C 1300: zero balance produces zero interest")
    void shouldReturnZeroInterestForZeroBalance() {
        // tcatbal records for accounts 1-5 all have balance=0.00
        // (0.00 * 15.00) / 1200 = 0.00
        BigDecimal result = interestCalculationService.computeMonthlyInterest(
            BigDecimal.ZERO, new BigDecimal("15.00")
        );
        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("CBACT04C 1300: zero rate produces zero interest")
    void shouldReturnZeroInterestForZeroRate() {
        // Disclosure group A000000000, type=02, cat=1 has rate=0.00
        // COBOL: IF DIS-INT-RATE NOT = 0 -> skip interest computation
        BigDecimal result = interestCalculationService.computeMonthlyInterest(
            new BigDecimal("5000.00"), BigDecimal.ZERO
        );
        assertThat(result).isEqualByComparingTo("0.00");
    }

    // -----------------------------------------------------------------------
    // Interest rate lookup tests (CBACT04C paragraphs 1200-GET-INTEREST-RATE
    // and 1200-A-GET-DEFAULT-INT-RATE)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CBACT04C 1200: looks up rate by account group ID")
    void shouldLookupInterestRateByGroupId() {
        // discgrp.txt: A000000000, type=01, cat=1 -> rate=15.00
        BigDecimal rate = interestCalculationService.lookupInterestRate(
            "A000000000", "01", 1
        );
        assertThat(rate).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("CBACT04C 1200: looks up rate for type=01 cat=2")
    void shouldLookupCashAdvanceRate() {
        // discgrp.txt: A000000000, type=01, cat=2 -> rate=25.00
        BigDecimal rate = interestCalculationService.lookupInterestRate(
            "A000000000", "01", 2
        );
        assertThat(rate).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("CBACT04C 1200-A: falls back to DEFAULT group when account group not found")
    void shouldFallBackToDefaultGroup() {
        // No disclosure group for "UNKNOWN" + type=01 + cat=1
        // Should fall back to DEFAULT, type=01, cat=1 -> rate=12.00
        BigDecimal rate = interestCalculationService.lookupInterestRate(
            "UNKNOWN", "01", 1
        );
        assertThat(rate).isEqualByComparingTo("12.00");
    }

    @Test
    @DisplayName("CBACT04C 1200: returns zero when neither group nor DEFAULT found")
    void shouldReturnZeroWhenNoRateFound() {
        // No disclosure group for "UNKNOWN" + type=99 + cat=99
        // No DEFAULT for that combo either
        BigDecimal rate = interestCalculationService.lookupInterestRate(
            "UNKNOWN", "99", 99
        );
        assertThat(rate).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("CBACT04C 1200: returns zero rate for payment-type transactions")
    void shouldReturnZeroRateForPaymentType() {
        // discgrp.txt: A000000000, type=02 (Payment) -> rate=0.00
        BigDecimal rate = interestCalculationService.lookupInterestRate(
            "A000000000", "02", 1
        );
        assertThat(rate).isEqualByComparingTo("0.00");
    }

    // -----------------------------------------------------------------------
    // Full interest calculation batch (CBACT04C main processing loop)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CBACT04C full: computes and applies interest for account 27 with sample data")
    void shouldCalculateAndApplyInterestForAccount27() {
        // Account 27: group=A000000000
        // tcatbal records:
        //   (27, '01', 1, 1200.00) -> rate 15.00 -> interest = (1200*15)/1200 = 15.00
        //   (27, '01', 2, 500.00)  -> rate 25.00 -> interest = (500*25)/1200 = 10.42
        // Total interest = 15.00 + 10.42 = 25.42
        //
        // Before: balance = 500.00
        // After:  balance = 500.00 + 25.42 = 525.42
        // Cycle credit and debit both reset to 0

        Account before = accountService.findById(27L).orElseThrow();
        BigDecimal originalBalance = before.getCurrentBalance();

        BigDecimal totalInterest = interestCalculationService.calculateAndApplyInterest(
            27L, LocalDate.of(2023, 7, 1)
        );

        // Verify interest matches COBOL formula
        BigDecimal expectedInt1 = new BigDecimal("1200.00").multiply(new BigDecimal("15.00"))
            .divide(new BigDecimal("1200"), 2, RoundingMode.HALF_UP);
        BigDecimal expectedInt2 = new BigDecimal("500.00").multiply(new BigDecimal("25.00"))
            .divide(new BigDecimal("1200"), 2, RoundingMode.HALF_UP);
        BigDecimal expectedTotal = expectedInt1.add(expectedInt2);

        assertThat(totalInterest).isEqualByComparingTo(expectedTotal);

        // Verify account update (CBACT04C 1050-UPDATE-ACCOUNT)
        Account after = accountService.findById(27L).orElseThrow();
        assertThat(after.getCurrentBalance())
            .isEqualByComparingTo(originalBalance.add(expectedTotal));
        assertThat(after.getCurrentCycleCredit()).isEqualByComparingTo("0.00");
        assertThat(after.getCurrentCycleDebit()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("CBACT04C: interest calculation skips zero-balance categories")
    void shouldProduceZeroInterestForZeroBalanceAccount() {
        // Account 1: tcatbal (1, '01', 1, 0.00) -> rate=15.00
        // COBOL: (0 * 15) / 1200 = 0; and the result IS computed (rate != 0)
        // but the monthly interest = 0.00 anyway
        BigDecimal totalInterest = interestCalculationService.calculateAndApplyInterest(
            1L, LocalDate.of(2023, 7, 1)
        );

        assertThat(totalInterest).isEqualByComparingTo("0.00");

        // Account balance should stay the same (0 interest added)
        Account after = accountService.findById(1L).orElseThrow();
        assertThat(after.getCurrentBalance()).isEqualByComparingTo("194.00");
        // Cycle accumulators should be reset
        assertThat(after.getCurrentCycleCredit()).isEqualByComparingTo("0.00");
        assertThat(after.getCurrentCycleDebit()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("CBACT04C 1300-B: interest transaction record is written correctly")
    void shouldWriteInterestTransactionRecord() {
        // Account 50: tcatbal (50, '01', 1, 3000.00) -> rate=15.00
        // interest = (3000 * 15) / 1200 = 37.50
        long txCountBefore = transactionRepository.count();

        interestCalculationService.calculateAndApplyInterest(
            50L, LocalDate.of(2023, 7, 1)
        );

        long txCountAfter = transactionRepository.count();
        // One interest transaction should be written
        assertThat(txCountAfter).isGreaterThan(txCountBefore);
    }

    @Test
    @DisplayName("CBACT04C: batch processes multiple accounts")
    void shouldProcessMultipleAccountsInBatch() {
        List<Long> processed = interestCalculationService.calculateInterestForAllAccounts(
            LocalDate.of(2023, 7, 1)
        );
        // Should process all accounts that have tcatbal records
        assertThat(processed).isNotEmpty();
    }
}
