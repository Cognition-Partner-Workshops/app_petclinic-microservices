package org.springframework.samples.petclinic.carddemo.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link TransactionCategoryBalance}.
 * Corresponds to the COBOL TRAN-CAT-KEY in copybook CVTRA01Y.
 */
public class TransactionCategoryBalanceId implements Serializable {

    private Long acctId;
    private String tranTypeCd;
    private Integer tranCatCd;

    public TransactionCategoryBalanceId() {
    }

    public TransactionCategoryBalanceId(Long acctId, String tranTypeCd, Integer tranCatCd) {
        this.acctId = acctId;
        this.tranTypeCd = tranTypeCd;
        this.tranCatCd = tranCatCd;
    }

    public Long getAcctId() {
        return acctId;
    }

    public String getTranTypeCd() {
        return tranTypeCd;
    }

    public Integer getTranCatCd() {
        return tranCatCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionCategoryBalanceId that)) return false;
        return Objects.equals(acctId, that.acctId)
            && Objects.equals(tranTypeCd, that.tranTypeCd)
            && Objects.equals(tranCatCd, that.tranCatCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acctId, tranTypeCd, tranCatCd);
    }
}
