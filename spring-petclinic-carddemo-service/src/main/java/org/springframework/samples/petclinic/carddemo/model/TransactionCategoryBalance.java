package org.springframework.samples.petclinic.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * JPA entity representing a transaction category balance.
 * Corresponds to the COBOL TRAN-CAT-BAL-RECORD copybook (CVTRA01Y).
 *
 * <p>The composite key is (acctId, tranTypeCd, tranCatCd), matching the
 * COBOL TRAN-CAT-KEY structure used in CBACT04C and CBTRN02C.
 */
@Entity
@Table(name = "tran_cat_balances")
@IdClass(TransactionCategoryBalanceId.class)
public class TransactionCategoryBalance {

    @Id
    @Column(name = "acct_id")
    private Long acctId;

    @Id
    @Column(name = "tran_type_cd", length = 2)
    private String tranTypeCd;

    @Id
    @Column(name = "tran_cat_cd")
    private Integer tranCatCd;

    @Column(name = "tran_cat_bal", precision = 11, scale = 2, nullable = false)
    private BigDecimal balance;

    protected TransactionCategoryBalance() {
    }

    public TransactionCategoryBalance(Long acctId, String tranTypeCd, Integer tranCatCd, BigDecimal balance) {
        this.acctId = acctId;
        this.tranTypeCd = tranTypeCd;
        this.tranCatCd = tranCatCd;
        this.balance = balance;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
