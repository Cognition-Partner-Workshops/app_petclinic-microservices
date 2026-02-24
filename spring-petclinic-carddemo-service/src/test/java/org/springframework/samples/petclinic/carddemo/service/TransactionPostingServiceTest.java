package org.springframework.samples.petclinic.carddemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.carddemo.model.Account;
import org.springframework.samples.petclinic.carddemo.model.Transaction;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalance;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalanceId;
import org.springframework.samples.petclinic.carddemo.repository.TransactionCategoryBalanceRepository;
import org.springframework.samples.petclinic.carddemo.repository.TransactionRepository;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comparison tests verifying that the Java TransactionPostingService produces
 * results matching the expected COBOL logic from CBTRN02C.
 *
 * <p>Tests use sample data from app/data/ASCII/ loaded via data.sql.
 */
@SpringBootTest
@Sql(scripts = {"/schema.sql", "/data.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TransactionPostingServiceTest {

    @Autowired
    private TransactionPostingService transactionPostingService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionCategoryBalanceRepository tcatBalRepository;

    // -----------------------------------------------------------------------
    // Validation tests (CBTRN02C paragraph 1500-VALIDATE-TRAN)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CBTRN02C 1500-A: validation fails with code 100 for unknown card number")
    void shouldRejectUnknownCardNumber() {
        // COBOL: MOVE 100 TO WS-VALIDATION-FAIL-REASON
        //        MOVE 'INVALID CARD NUMBER FOUND' TO WS-VALIDATION-FAIL-REASON-DESC
        TransactionValidationResult result = transactionPostingService.validateTransaction(
            "9999999999999999",
            new BigDecimal("100.00"),
            LocalDate.of(2023, 6, 10)
        );
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo(TransactionValidationResult.INVALID_CARD_NUMBER);
        assertThat(result.getFailureDescription()).isEqualTo("INVALID CARD NUMBER FOUND");
    }

    @Test
    @DisplayName("CBTRN02C 1500-B: validation fails with code 102 for over-limit transaction")
    void shouldRejectOverLimitTransaction() {
        // Card 0500024453765740 -> account 50 (credit_limit=5000, cyc_credit=200, cyc_debit=-100)
        // COBOL: WS-TEMP-BAL = 200 - (-100) + 6000 = 6300
        //        5000 >= 6300 -> false
        // MOVE 102 TO WS-VALIDATION-FAIL-REASON
        TransactionValidationResult result = transactionPostingService.validateTransaction(
            "0500024453765740",
            new BigDecimal("6000.00"),
            LocalDate.of(2023, 6, 10)
        );
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo(TransactionValidationResult.OVER_LIMIT);
    }

    @Test
    @DisplayName("CBTRN02C 1500-B: validation fails with code 103 for expired account")
    void shouldRejectExpiredAccount() {
        // Card 0923877193247330 -> account 2 (expiration_date=2024-08-11)
        // COBOL: IF ACCT-EXPIRAION-DATE >= DALYTRAN-ORIG-TS(1:10) -> OK
        //        2024-08-11 >= 2025-01-01 -> false
        // MOVE 103 TO WS-VALIDATION-FAIL-REASON
        TransactionValidationResult result = transactionPostingService.validateTransaction(
            "0923877193247330",
            new BigDecimal("50.00"),
            LocalDate.of(2025, 1, 1)
        );
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFailureReason()).isEqualTo(TransactionValidationResult.ACCOUNT_EXPIRED);
    }

    @Test
    @DisplayName("CBTRN02C 1500: validation passes for valid transaction within limits")
    void shouldAcceptValidTransaction() {
        // Card 0683586198171516 -> account 27 (credit_limit=10000, exp=2025-12-31)
        // cyc_credit=100, cyc_debit=-50
        // WS-TEMP-BAL = 100 - (-50) + 500 = 650; 10000 >= 650 -> true
        // 2025-12-31 >= 2023-06-10 -> true
        TransactionValidationResult result = transactionPostingService.validateTransaction(
            "0683586198171516",
            new BigDecimal("500.00"),
            LocalDate.of(2023, 6, 10)
        );
        assertThat(result.isValid()).isTrue();
        assertThat(result.getFailureReason()).isEqualTo(TransactionValidationResult.VALID);
    }

    // -----------------------------------------------------------------------
    // Transaction posting tests (CBTRN02C paragraph 2000-POST-TRANSACTION)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("CBTRN02C 2000: post positive transaction updates account and tcatbal")
    void shouldPostPositiveTransactionCorrectly() {
        // Post a positive (credit) transaction to account 27 via card 0683586198171516
        // COBOL flow: 2700-UPDATE-TCATBAL, 2800-UPDATE-ACCOUNT-REC, 2900-WRITE-TRANSACTION-FILE

        Account beforeAccount = accountService.findById(27L).orElseThrow();
        BigDecimal originalBalance = beforeAccount.getCurrentBalance();
        BigDecimal originalCycCredit = beforeAccount.getCurrentCycleCredit();

        BigDecimal txnAmt = new BigDecimal("250.75");
        Transaction txn = transactionPostingService.postTransaction(
            "TX00000000000001", "01", 1,
            "POS TERM", "Test purchase", txnAmt,
            800000000L, "Test Merchant", "Test City", "12345",
            "0683586198171516",
            LocalDateTime.of(2023, 6, 10, 19, 27, 53)
        );

        // 2900: Transaction record should be written
        assertThat(txn).isNotNull();
        assertThat(txn.getTranId()).isEqualTo("TX00000000000001");
        assertThat(txn.getTranAmt()).isEqualByComparingTo("250.75");

        // 2800: Account balance should be updated
        // ADD DALYTRAN-AMT TO ACCT-CURR-BAL -> 500.00 + 250.75 = 750.75
        Account afterAccount = accountService.findById(27L).orElseThrow();
        assertThat(afterAccount.getCurrentBalance())
            .isEqualByComparingTo(originalBalance.add(txnAmt));
        // Positive amount -> ADD to ACCT-CURR-CYC-CREDIT
        assertThat(afterAccount.getCurrentCycleCredit())
            .isEqualByComparingTo(originalCycCredit.add(txnAmt));

        // 2700: Transaction category balance should be updated
        // ADD DALYTRAN-AMT TO TRAN-CAT-BAL -> 1200.00 + 250.75 = 1450.75
        TransactionCategoryBalanceId tcatId = new TransactionCategoryBalanceId(27L, "01", 1);
        TransactionCategoryBalance tcatBal = tcatBalRepository.findById(tcatId).orElseThrow();
        assertThat(tcatBal.getBalance()).isEqualByComparingTo("1450.75");
    }

    @Test
    @DisplayName("CBTRN02C 2700-A: creates new tcatbal record when one does not exist")
    void shouldCreateNewTcatBalRecord() {
        // Post a transaction with a type/category combo that doesn't exist yet
        // COBOL: 2700-A-CREATE-TCATBAL-REC - WRITE FD-TRAN-CAT-BAL-RECORD
        BigDecimal txnAmt = new BigDecimal("123.45");
        transactionPostingService.postTransaction(
            "TX00000000000002", "03", 9,
            "OPERATOR", "New category test", txnAmt,
            0L, "", "", "",
            "0683586198171516",
            LocalDateTime.of(2023, 6, 10, 12, 0, 0)
        );

        // New tcatbal record should be created with balance = txnAmt
        TransactionCategoryBalanceId tcatId = new TransactionCategoryBalanceId(27L, "03", 9);
        TransactionCategoryBalance tcatBal = tcatBalRepository.findById(tcatId).orElseThrow();
        assertThat(tcatBal.getBalance()).isEqualByComparingTo("123.45");
    }

    @Test
    @DisplayName("CBTRN02C 2800: negative transaction adds to cycle debit, not credit")
    void shouldPostNegativeTransactionCorrectly() {
        // Post a negative (debit) transaction to account 50 via card 0500024453765740
        Account beforeAccount = accountService.findById(50L).orElseThrow();
        BigDecimal originalDebit = beforeAccount.getCurrentCycleDebit();
        BigDecimal originalCredit = beforeAccount.getCurrentCycleCredit();

        BigDecimal txnAmt = new BigDecimal("-300.00");
        transactionPostingService.postTransaction(
            "TX00000000000003", "01", 1,
            "POS TERM", "Debit test", txnAmt,
            0L, "", "", "",
            "0500024453765740",
            LocalDateTime.of(2023, 6, 10, 15, 0, 0)
        );

        Account afterAccount = accountService.findById(50L).orElseThrow();
        // COBOL: negative -> ADD DALYTRAN-AMT TO ACCT-CURR-CYC-DEBIT
        assertThat(afterAccount.getCurrentCycleDebit())
            .isEqualByComparingTo(originalDebit.add(txnAmt));
        // Credit should be unchanged
        assertThat(afterAccount.getCurrentCycleCredit())
            .isEqualByComparingTo(originalCredit);
    }
}
