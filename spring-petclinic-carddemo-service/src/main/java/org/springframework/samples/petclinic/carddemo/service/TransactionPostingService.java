package org.springframework.samples.petclinic.carddemo.service;

import org.springframework.samples.petclinic.carddemo.model.Account;
import org.springframework.samples.petclinic.carddemo.model.CardXref;
import org.springframework.samples.petclinic.carddemo.model.Transaction;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalance;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalanceId;
import org.springframework.samples.petclinic.carddemo.repository.CardXrefRepository;
import org.springframework.samples.petclinic.carddemo.repository.TransactionCategoryBalanceRepository;
import org.springframework.samples.petclinic.carddemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service that implements the daily-transaction posting logic from CBTRN02C.
 *
 * <p>The COBOL flow for each daily transaction record is:
 * <ol>
 *   <li>1500-VALIDATE-TRAN &rarr; look up XREF, look up account, check credit limit, check expiration</li>
 *   <li>2000-POST-TRANSACTION &rarr; update TCATBAL, update account, write transaction record</li>
 * </ol>
 *
 * <p>Validation failure codes (from CBTRN02C WS-VALIDATION-FAIL-REASON):
 * <ul>
 *   <li>100 - Card number not found in XREF</li>
 *   <li>101 - Account record not found</li>
 *   <li>102 - Over-limit: ACCT-CREDIT-LIMIT &lt; (CYC_CREDIT - CYC_DEBIT + TRAN_AMT)</li>
 *   <li>103 - Account expired: ACCT-EXPIRAION-DATE &lt; transaction origination date</li>
 * </ul>
 */
@Service
public class TransactionPostingService {

    private final CardXrefRepository cardXrefRepository;
    private final AccountService accountService;
    private final TransactionCategoryBalanceRepository tcatBalRepository;
    private final TransactionRepository transactionRepository;

    public TransactionPostingService(CardXrefRepository cardXrefRepository,
                                     AccountService accountService,
                                     TransactionCategoryBalanceRepository tcatBalRepository,
                                     TransactionRepository transactionRepository) {
        this.cardXrefRepository = cardXrefRepository;
        this.accountService = accountService;
        this.tcatBalRepository = tcatBalRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Validates a daily transaction against the card cross-reference and account data.
     * Mirrors CBTRN02C paragraphs 1500-VALIDATE-TRAN, 1500-A-LOOKUP-XREF, 1500-B-LOOKUP-ACCT.
     *
     * @param cardNum            the 16-character card number from the daily transaction
     * @param transactionAmount  the signed transaction amount
     * @param transactionOrigDate the origination date of the transaction
     * @return validation result with failure code and description
     */
    public TransactionValidationResult validateTransaction(String cardNum,
                                                           BigDecimal transactionAmount,
                                                           LocalDate transactionOrigDate) {
        // 1500-A-LOOKUP-XREF: look up card in cross-reference
        Optional<CardXref> xrefOpt = cardXrefRepository.findById(cardNum);
        if (xrefOpt.isEmpty()) {
            return TransactionValidationResult.invalidCard();
        }

        CardXref xref = xrefOpt.get();

        // 1500-B-LOOKUP-ACCT: look up account
        Optional<Account> acctOpt = accountService.findById(xref.getAcctId());
        if (acctOpt.isEmpty()) {
            return TransactionValidationResult.accountNotFound();
        }

        Account account = acctOpt.get();

        // Credit limit check:
        // COMPUTE WS-TEMP-BAL = ACCT-CURR-CYC-CREDIT - ACCT-CURR-CYC-DEBIT + DALYTRAN-AMT
        // IF ACCT-CREDIT-LIMIT >= WS-TEMP-BAL -> OK
        if (!accountService.isWithinCreditLimit(account, transactionAmount)) {
            return TransactionValidationResult.overLimit();
        }

        // Expiration check:
        // IF ACCT-EXPIRAION-DATE >= DALYTRAN-ORIG-TS(1:10) -> OK
        if (account.getExpirationDate() != null
            && account.getExpirationDate().isBefore(transactionOrigDate)) {
            return TransactionValidationResult.accountExpired();
        }

        return TransactionValidationResult.valid();
    }

    /**
     * Posts a validated daily transaction.
     * Mirrors CBTRN02C paragraph 2000-POST-TRANSACTION, which calls:
     * <ul>
     *   <li>2700-UPDATE-TCATBAL - create or update transaction category balance</li>
     *   <li>2800-UPDATE-ACCOUNT-REC - update account balances</li>
     *   <li>2900-WRITE-TRANSACTION-FILE - write the transaction record</li>
     * </ul>
     *
     * @param tranId        unique transaction identifier
     * @param tranTypeCd    2-character transaction type code
     * @param tranCatCd     4-digit transaction category code
     * @param tranSource    transaction source (e.g., "POS TERM")
     * @param tranDesc      transaction description
     * @param tranAmt       signed transaction amount
     * @param merchantId    merchant identifier
     * @param merchantName  merchant name
     * @param merchantCity  merchant city
     * @param merchantZip   merchant ZIP code
     * @param cardNum       16-character card number
     * @param origTs        original timestamp of the transaction
     * @return the posted Transaction entity
     */
    @Transactional
    public Transaction postTransaction(String tranId, String tranTypeCd, Integer tranCatCd,
                                       String tranSource, String tranDesc, BigDecimal tranAmt,
                                       Long merchantId, String merchantName, String merchantCity,
                                       String merchantZip, String cardNum, LocalDateTime origTs) {
        CardXref xref = cardXrefRepository.findById(cardNum)
            .orElseThrow(() -> new IllegalStateException("Card XREF not found for: " + cardNum));

        Account account = accountService.findById(xref.getAcctId())
            .orElseThrow(() -> new IllegalStateException("Account not found: " + xref.getAcctId()));

        // 2700-UPDATE-TCATBAL: update transaction category balance
        updateTransactionCategoryBalance(xref.getAcctId(), tranTypeCd, tranCatCd, tranAmt);

        // 2800-UPDATE-ACCOUNT-REC: update account balances
        accountService.applyTransactionToBalance(account, tranAmt);

        // 2900-WRITE-TRANSACTION-FILE: write the transaction record
        LocalDateTime procTs = LocalDateTime.now();
        Transaction transaction = new Transaction(
            tranId, tranTypeCd, tranCatCd, tranSource, tranDesc, tranAmt,
            merchantId, merchantName, merchantCity, merchantZip,
            cardNum, origTs, procTs
        );
        return transactionRepository.save(transaction);
    }

    /**
     * Creates or updates the transaction category balance record.
     * Mirrors CBTRN02C paragraphs 2700-UPDATE-TCATBAL, 2700-A-CREATE-TCATBAL-REC,
     * and 2700-B-UPDATE-TCATBAL-REC.
     *
     * <p>COBOL logic:
     * <pre>
     *   READ TCATBAL-FILE INTO TRAN-CAT-BAL-RECORD
     *     INVALID KEY  -&gt; create new record with balance = tranAmt
     *     NOT INVALID  -&gt; ADD DALYTRAN-AMT TO TRAN-CAT-BAL, then REWRITE
     * </pre>
     */
    private void updateTransactionCategoryBalance(Long acctId, String tranTypeCd,
                                                  Integer tranCatCd, BigDecimal tranAmt) {
        TransactionCategoryBalanceId id = new TransactionCategoryBalanceId(acctId, tranTypeCd, tranCatCd);
        Optional<TransactionCategoryBalance> existing = tcatBalRepository.findById(id);

        if (existing.isPresent()) {
            // 2700-B: ADD DALYTRAN-AMT TO TRAN-CAT-BAL
            TransactionCategoryBalance tcatBal = existing.get();
            tcatBal.setBalance(tcatBal.getBalance().add(tranAmt));
            tcatBalRepository.save(tcatBal);
        } else {
            // 2700-A: create new record, balance = tranAmt
            TransactionCategoryBalance tcatBal = new TransactionCategoryBalance(
                acctId, tranTypeCd, tranCatCd, tranAmt
            );
            tcatBalRepository.save(tcatBal);
        }
    }
}
