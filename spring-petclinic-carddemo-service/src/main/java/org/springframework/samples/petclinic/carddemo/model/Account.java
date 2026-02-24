package org.springframework.samples.petclinic.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity representing a credit card account.
 * Corresponds to the COBOL ACCOUNT-RECORD copybook (CVACT01Y).
 *
 * <p>Fields map to the COBOL PIC clauses:
 * <ul>
 *   <li>ACCT-ID: PIC 9(11) &rarr; Long</li>
 *   <li>ACCT-CURR-BAL: PIC S9(10)V99 &rarr; BigDecimal(12,2)</li>
 *   <li>ACCT-CREDIT-LIMIT: PIC S9(10)V99 &rarr; BigDecimal(12,2)</li>
 * </ul>
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "acct_id")
    private Long acctId;

    @Column(name = "active_status", length = 1, nullable = false)
    private String activeStatus;

    @Column(name = "curr_bal", precision = 12, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "credit_limit", precision = 12, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "cash_credit_limit", precision = 12, scale = 2, nullable = false)
    private BigDecimal cashCreditLimit;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "reissue_date")
    private LocalDate reissueDate;

    @Column(name = "curr_cyc_credit", precision = 12, scale = 2, nullable = false)
    private BigDecimal currentCycleCredit;

    @Column(name = "curr_cyc_debit", precision = 12, scale = 2, nullable = false)
    private BigDecimal currentCycleDebit;

    @Column(name = "group_id", length = 10)
    private String groupId;

    protected Account() {
    }

    public Account(Long acctId, String activeStatus, BigDecimal currentBalance,
                   BigDecimal creditLimit, BigDecimal cashCreditLimit,
                   LocalDate openDate, LocalDate expirationDate, LocalDate reissueDate,
                   BigDecimal currentCycleCredit, BigDecimal currentCycleDebit,
                   String groupId) {
        this.acctId = acctId;
        this.activeStatus = activeStatus;
        this.currentBalance = currentBalance;
        this.creditLimit = creditLimit;
        this.cashCreditLimit = cashCreditLimit;
        this.openDate = openDate;
        this.expirationDate = expirationDate;
        this.reissueDate = reissueDate;
        this.currentCycleCredit = currentCycleCredit;
        this.currentCycleDebit = currentCycleDebit;
        this.groupId = groupId;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCashCreditLimit() {
        return cashCreditLimit;
    }

    public void setCashCreditLimit(BigDecimal cashCreditLimit) {
        this.cashCreditLimit = cashCreditLimit;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getReissueDate() {
        return reissueDate;
    }

    public void setReissueDate(LocalDate reissueDate) {
        this.reissueDate = reissueDate;
    }

    public BigDecimal getCurrentCycleCredit() {
        return currentCycleCredit;
    }

    public void setCurrentCycleCredit(BigDecimal currentCycleCredit) {
        this.currentCycleCredit = currentCycleCredit;
    }

    public BigDecimal getCurrentCycleDebit() {
        return currentCycleDebit;
    }

    public void setCurrentCycleDebit(BigDecimal currentCycleDebit) {
        this.currentCycleDebit = currentCycleDebit;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
