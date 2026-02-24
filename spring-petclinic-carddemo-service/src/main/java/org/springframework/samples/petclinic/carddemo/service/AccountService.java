package org.springframework.samples.petclinic.carddemo.service;

import org.springframework.samples.petclinic.carddemo.model.Account;
import org.springframework.samples.petclinic.carddemo.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service encapsulating account-level business logic extracted from COBOL programs.
 *
 * <h3>Balance update (CBTRN02C paragraph 2800-UPDATE-ACCOUNT-REC)</h3>
 * <pre>
 *   ADD DALYTRAN-AMT TO ACCT-CURR-BAL
 *   IF DALYTRAN-AMT &gt;= 0
 *      ADD DALYTRAN-AMT TO ACCT-CURR-CYC-CREDIT
 *   ELSE
 *      ADD DALYTRAN-AMT TO ACCT-CURR-CYC-DEBIT
 * </pre>
 *
 * <h3>Cycle reset (CBACT04C paragraph 1050-UPDATE-ACCOUNT)</h3>
 * <pre>
 *   ADD WS-TOTAL-INT TO ACCT-CURR-BAL
 *   MOVE 0 TO ACCT-CURR-CYC-CREDIT
 *   MOVE 0 TO ACCT-CURR-CYC-DEBIT
 * </pre>
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> findById(Long acctId) {
        return accountRepository.findById(acctId);
    }

    /**
     * Updates account balances after a transaction is posted.
     * Mirrors CBTRN02C paragraph 2800-UPDATE-ACCOUNT-REC.
     *
     * @param account the account to update
     * @param transactionAmount the signed transaction amount (positive = credit, negative = debit)
     */
    @Transactional
    public void applyTransactionToBalance(Account account, BigDecimal transactionAmount) {
        // ADD DALYTRAN-AMT TO ACCT-CURR-BAL
        account.setCurrentBalance(account.getCurrentBalance().add(transactionAmount));

        // IF DALYTRAN-AMT >= 0
        //   ADD DALYTRAN-AMT TO ACCT-CURR-CYC-CREDIT
        // ELSE
        //   ADD DALYTRAN-AMT TO ACCT-CURR-CYC-DEBIT
        if (transactionAmount.compareTo(BigDecimal.ZERO) >= 0) {
            account.setCurrentCycleCredit(account.getCurrentCycleCredit().add(transactionAmount));
        } else {
            account.setCurrentCycleDebit(account.getCurrentCycleDebit().add(transactionAmount));
        }

        accountRepository.save(account);
    }

    /**
     * Applies accumulated interest to the account balance and resets cycle accumulators.
     * Mirrors CBACT04C paragraph 1050-UPDATE-ACCOUNT.
     *
     * @param account the account to update
     * @param totalInterest the total interest computed across all category balances
     */
    @Transactional
    public void applyInterestAndResetCycle(Account account, BigDecimal totalInterest) {
        // ADD WS-TOTAL-INT TO ACCT-CURR-BAL
        account.setCurrentBalance(account.getCurrentBalance().add(totalInterest));

        // MOVE 0 TO ACCT-CURR-CYC-CREDIT
        // MOVE 0 TO ACCT-CURR-CYC-DEBIT
        account.setCurrentCycleCredit(BigDecimal.ZERO);
        account.setCurrentCycleDebit(BigDecimal.ZERO);

        accountRepository.save(account);
    }

    /**
     * Checks whether a proposed transaction would exceed the account credit limit.
     * Mirrors CBTRN02C paragraph 1500-B-LOOKUP-ACCT credit-limit validation:
     * <pre>
     *   COMPUTE WS-TEMP-BAL = ACCT-CURR-CYC-CREDIT
     *                       - ACCT-CURR-CYC-DEBIT
     *                       + DALYTRAN-AMT
     *   IF ACCT-CREDIT-LIMIT &gt;= WS-TEMP-BAL  -&gt; OK
     * </pre>
     *
     * @return true if the transaction is within the credit limit
     */
    public boolean isWithinCreditLimit(Account account, BigDecimal transactionAmount) {
        BigDecimal tempBalance = account.getCurrentCycleCredit()
            .subtract(account.getCurrentCycleDebit())
            .add(transactionAmount);
        return account.getCreditLimit().compareTo(tempBalance) >= 0;
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
