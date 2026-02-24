package org.springframework.samples.petclinic.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * JPA entity representing an interest rate disclosure group.
 * Corresponds to the COBOL DIS-GROUP-RECORD copybook (CVTRA02Y).
 *
 * <p>Used by the interest calculation logic in CBACT04C to look up
 * the annual interest rate for a given account group, transaction type,
 * and transaction category. Falls back to group ID "DEFAULT" if the
 * account-specific group is not found.
 */
@Entity
@Table(name = "disclosure_groups")
@IdClass(DisclosureGroupId.class)
public class DisclosureGroup {

    @Id
    @Column(name = "acct_group_id", length = 10)
    private String acctGroupId;

    @Id
    @Column(name = "tran_type_cd", length = 2)
    private String tranTypeCd;

    @Id
    @Column(name = "tran_cat_cd")
    private Integer tranCatCd;

    /**
     * Annual interest rate as a percentage (e.g. 15.00 means 15% APR).
     * Corresponds to COBOL DIS-INT-RATE: PIC S9(04)V99.
     */
    @Column(name = "int_rate", precision = 6, scale = 2, nullable = false)
    private BigDecimal interestRate;

    protected DisclosureGroup() {
    }

    public DisclosureGroup(String acctGroupId, String tranTypeCd, Integer tranCatCd, BigDecimal interestRate) {
        this.acctGroupId = acctGroupId;
        this.tranTypeCd = tranTypeCd;
        this.tranCatCd = tranCatCd;
        this.interestRate = interestRate;
    }

    public String getAcctGroupId() {
        return acctGroupId;
    }

    public void setAcctGroupId(String acctGroupId) {
        this.acctGroupId = acctGroupId;
    }

    public String getTranTypeCd() {
        return tranTypeCd;
    }

    public void setTranTypeCd(String tranTypeCd) {
        this.tranTypeCd = tranTypeCd;
    }

    public Integer getTranCatCd() {
        return tranCatCd;
    }

    public void setTranCatCd(Integer tranCatCd) {
        this.tranCatCd = tranCatCd;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
