package org.springframework.samples.petclinic.carddemo.service;

import org.springframework.samples.petclinic.carddemo.model.Account;
import org.springframework.samples.petclinic.carddemo.model.CardXref;
import org.springframework.samples.petclinic.carddemo.model.DisclosureGroup;
import org.springframework.samples.petclinic.carddemo.model.DisclosureGroupId;
import org.springframework.samples.petclinic.carddemo.model.Transaction;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalance;
import org.springframework.samples.petclinic.carddemo.repository.CardXrefRepository;
import org.springframework.samples.petclinic.carddemo.repository.DisclosureGroupRepository;
import org.springframework.samples.petclinic.carddemo.repository.TransactionCategoryBalanceRepository;
import org.springframework.samples.petclinic.carddemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service that implements the interest calculation logic from CBACT04C.
 *
 * <h3>Core interest formula (CBACT04C paragraph 1300-COMPUTE-INTEREST)</h3>
 * <pre>
 *   COMPUTE WS-MONTHLY-INT = (TRAN-CAT-BAL * DIS-INT-RATE) / 1200
 * </pre>
 *
 * <p>The divisor 1200 converts an annual percentage rate to a monthly amount:
 * dividing by 100 converts from percentage to decimal, then dividing by 12
 * gives the monthly fraction.
 *
 * <h3>Interest rate lookup (CBACT04C paragraph 1200-GET-INTEREST-RATE)</h3>
 * <p>First attempts to find the rate using the account's group ID. If not found
 * (COBOL status '23'), falls back to group ID "DEFAULT".
 *
 * <h3>Account update (CBACT04C paragraph 1050-UPDATE-ACCOUNT)</h3>
 * <pre>
 *   ADD WS-TOTAL-INT TO ACCT-CURR-BAL
 *   MOVE 0 TO ACCT-CURR-CYC-CREDIT
 *   MOVE 0 TO ACCT-CURR-CYC-DEBIT
 * </pre>
 */
@Service
public class InterestCalculationService {

    /** The COBOL divisor: annual rate / 100 / 12 = rate / 1200. */
    private static final BigDecimal MONTHLY_DIVISOR = new BigDecimal("1200");

    private static final String DEFAULT_GROUP_ID = "DEFAULT";
    private static final String INTEREST_TRAN_TYPE = "01";
    private static final int INTEREST_TRAN_CAT = 5;

    private final TransactionCategoryBalanceRepository tcatBalRepository;
    private final DisclosureGroupRepository disclosureGroupRepository;
    private final CardXrefRepository cardXrefRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public InterestCalculationService(TransactionCategoryBalanceRepository tcatBalRepository,
                                      DisclosureGroupRepository disclosureGroupRepository,
                                      CardXrefRepository cardXrefRepository,
                                      AccountService accountService,
                                      TransactionRepository transactionRepository) {
        this.tcatBalRepository = tcatBalRepository;
        this.disclosureGroupRepository = disclosureGroupRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Computes monthly interest for a single transaction category balance.
     * Mirrors CBACT04C paragraph 1300-COMPUTE-INTEREST:
     * <pre>
     *   COMPUTE WS-MONTHLY-INT = (TRAN-CAT-BAL * DIS-INT-RATE) / 1200
     * </pre>
     *
     * @param categoryBalance the balance for one transaction category
     * @param annualRate      the annual interest rate as a percentage (e.g. 15.00)
     * @return the monthly interest amount, scaled to 2 decimal places
     */
    public BigDecimal computeMonthlyInterest(BigDecimal categoryBalance, BigDecimal annualRate) {
        return categoryBalance.multiply(annualRate)
            .divide(MONTHLY_DIVISOR, 2, RoundingMode.HALF_UP);
    }

    /**
     * Looks up the interest rate for a given account group, transaction type, and category.
     * Mirrors CBACT04C paragraphs 1200-GET-INTEREST-RATE and 1200-A-GET-DEFAULT-INT-RATE.
     *
     * <p>If no rate is found for the account's specific group ID, falls back to "DEFAULT".
     *
     * @param acctGroupId the account's group ID (e.g. "A000000000")
     * @param tranTypeCd  the 2-character transaction type code
     * @param tranCatCd   the 4-digit transaction category code
     * @return the annual interest rate, or {@link BigDecimal#ZERO} if not found
     */
    public BigDecimal lookupInterestRate(String acctGroupId, String tranTypeCd, Integer tranCatCd) {
        // First try with the account's group ID
        DisclosureGroupId primaryId = new DisclosureGroupId(acctGroupId, tranTypeCd, tranCatCd);
        Optional<DisclosureGroup> primary = disclosureGroupRepository.findById(primaryId);
        if (primary.isPresent()) {
            return primary.get().getInterestRate();
        }

        // Fall back to DEFAULT group (CBACT04C: MOVE 'DEFAULT' TO FD-DIS-ACCT-GROUP-ID)
        DisclosureGroupId defaultId = new DisclosureGroupId(DEFAULT_GROUP_ID, tranTypeCd, tranCatCd);
        Optional<DisclosureGroup> defaultGroup = disclosureGroupRepository.findById(defaultId);
        return defaultGroup.map(DisclosureGroup::getInterestRate).orElse(BigDecimal.ZERO);
    }

    /**
     * Runs the full interest calculation batch for a single account.
     * Mirrors the main processing loop of CBACT04C for one account.
     *
     * <p>For each transaction category balance belonging to the account:
     * <ol>
     *   <li>Look up the interest rate (with DEFAULT fallback)</li>
     *   <li>If rate != 0, compute monthly interest and accumulate</li>
     *   <li>Write an interest transaction record</li>
     * </ol>
     * <p>After processing all categories, update the account balance and reset cycle accumulators.
     *
     * @param acctId   the account ID to process
     * @param parmDate the batch parameter date, used to generate transaction IDs
     * @return the total interest amount applied to the account
     */
    @Transactional
    public BigDecimal calculateAndApplyInterest(Long acctId, LocalDate parmDate) {
        Account account = accountService.findById(acctId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + acctId));

        Optional<CardXref> xrefOpt = cardXrefRepository.findFirstByAcctId(acctId);
        String cardNum = xrefOpt.map(CardXref::getCardNum).orElse("");

        List<TransactionCategoryBalance> balances = tcatBalRepository.findByAcctId(acctId);

        BigDecimal totalInterest = BigDecimal.ZERO;
        AtomicInteger tranIdSuffix = new AtomicInteger(0);

        for (TransactionCategoryBalance tcatBal : balances) {
            BigDecimal rate = lookupInterestRate(
                account.getGroupId(),
                tcatBal.getTranTypeCd(),
                tcatBal.getTranCatCd()
            );

            // CBACT04C: IF DIS-INT-RATE NOT = 0
            if (rate.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal monthlyInterest = computeMonthlyInterest(tcatBal.getBalance(), rate);
                totalInterest = totalInterest.add(monthlyInterest);

                // 1300-B-WRITE-TX: write interest transaction
                writeInterestTransaction(acctId, cardNum, monthlyInterest, parmDate,
                    tranIdSuffix.incrementAndGet());
            }
        }

        // 1050-UPDATE-ACCOUNT: apply interest and reset cycle
        accountService.applyInterestAndResetCycle(account, totalInterest);

        return totalInterest;
    }

    /**
     * Processes interest for all accounts that have transaction category balances.
     * Mirrors the main loop of CBACT04C across all accounts.
     *
     * @param parmDate the batch parameter date
     * @return list of account IDs processed
     */
    @Transactional
    public List<Long> calculateInterestForAllAccounts(LocalDate parmDate) {
        List<TransactionCategoryBalance> allBalances = tcatBalRepository.findAll();
        List<Long> processedAccounts = new ArrayList<>();
        Long lastAcctId = null;

        for (TransactionCategoryBalance bal : allBalances) {
            if (!bal.getAcctId().equals(lastAcctId)) {
                if (lastAcctId != null) {
                    // Already processed via calculateAndApplyInterest
                }
                lastAcctId = bal.getAcctId();
                if (!processedAccounts.contains(lastAcctId)) {
                    calculateAndApplyInterest(lastAcctId, parmDate);
                    processedAccounts.add(lastAcctId);
                }
            }
        }

        return processedAccounts;
    }

    /**
     * Writes an interest transaction record.
     * Mirrors CBACT04C paragraph 1300-B-WRITE-TX.
     */
    private void writeInterestTransaction(Long acctId, String cardNum,
                                          BigDecimal monthlyInterest,
                                          LocalDate parmDate, int suffix) {
        // STRING PARM-DATE, WS-TRANID-SUFFIX INTO TRAN-ID
        String tranId = String.format("%s%06d", parmDate.toString().replace("-", ""), suffix);
        // Pad/truncate to 16 characters to match COBOL PIC X(16)
        if (tranId.length() > 16) {
            tranId = tranId.substring(0, 16);
        } else {
            tranId = String.format("%-16s", tranId);
        }

        String tranDesc = "Int. for a/c " + String.format("%011d", acctId);
        LocalDateTime now = LocalDateTime.now();

        Transaction interestTx = new Transaction(
            tranId,
            INTEREST_TRAN_TYPE,    // MOVE '01' TO TRAN-TYPE-CD
            INTEREST_TRAN_CAT,     // MOVE '05' TO TRAN-CAT-CD (mapped to integer 5)
            "System",              // MOVE 'System' TO TRAN-SOURCE
            tranDesc,
            monthlyInterest,
            0L,                    // MOVE 0 TO TRAN-MERCHANT-ID
            "",                    // MOVE SPACES TO TRAN-MERCHANT-NAME
            "",                    // MOVE SPACES TO TRAN-MERCHANT-CITY
            "",                    // MOVE SPACES TO TRAN-MERCHANT-ZIP
            cardNum,               // MOVE XREF-CARD-NUM TO TRAN-CARD-NUM
            now,                   // MOVE DB2-FORMAT-TS TO TRAN-ORIG-TS
            now                    // MOVE DB2-FORMAT-TS TO TRAN-PROC-TS
        );

        transactionRepository.save(interestTx);
    }
}
