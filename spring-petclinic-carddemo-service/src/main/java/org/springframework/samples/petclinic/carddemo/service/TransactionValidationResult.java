package org.springframework.samples.petclinic.carddemo.service;

/**
 * Result of validating a daily transaction before posting.
 * Mirrors the COBOL validation logic in CBTRN02C paragraph 1500-VALIDATE-TRAN.
 *
 * <p>Failure reason codes match the COBOL WS-VALIDATION-FAIL-REASON values:
 * <ul>
 *   <li>0 - Valid</li>
 *   <li>100 - Invalid card number (XREF lookup failed)</li>
 *   <li>101 - Account record not found</li>
 *   <li>102 - Over-limit transaction</li>
 *   <li>103 - Transaction received after account expiration</li>
 * </ul>
 */
public class TransactionValidationResult {

    public static final int VALID = 0;
    public static final int INVALID_CARD_NUMBER = 100;
    public static final int ACCOUNT_NOT_FOUND = 101;
    public static final int OVER_LIMIT = 102;
    public static final int ACCOUNT_EXPIRED = 103;

    private final int failureReason;
    private final String failureDescription;

    private TransactionValidationResult(int failureReason, String failureDescription) {
        this.failureReason = failureReason;
        this.failureDescription = failureDescription;
    }

    public static TransactionValidationResult valid() {
        return new TransactionValidationResult(VALID, "");
    }

    public static TransactionValidationResult invalidCard() {
        return new TransactionValidationResult(INVALID_CARD_NUMBER, "INVALID CARD NUMBER FOUND");
    }

    public static TransactionValidationResult accountNotFound() {
        return new TransactionValidationResult(ACCOUNT_NOT_FOUND, "ACCOUNT RECORD NOT FOUND");
    }

    public static TransactionValidationResult overLimit() {
        return new TransactionValidationResult(OVER_LIMIT, "OVERLIMIT TRANSACTION");
    }

    public static TransactionValidationResult accountExpired() {
        return new TransactionValidationResult(ACCOUNT_EXPIRED, "TRANSACTION RECEIVED AFTER ACCT EXPIRATION");
    }

    public boolean isValid() {
        return failureReason == VALID;
    }

    public int getFailureReason() {
        return failureReason;
    }

    public String getFailureDescription() {
        return failureDescription;
    }
}
