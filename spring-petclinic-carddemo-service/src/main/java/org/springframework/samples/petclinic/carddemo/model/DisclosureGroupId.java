package org.springframework.samples.petclinic.carddemo.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link DisclosureGroup}.
 * Corresponds to the COBOL DIS-GROUP-KEY in copybook CVTRA02Y.
 */
public class DisclosureGroupId implements Serializable {

    private String acctGroupId;
    private String tranTypeCd;
    private Integer tranCatCd;

    public DisclosureGroupId() {
    }

    public DisclosureGroupId(String acctGroupId, String tranTypeCd, Integer tranCatCd) {
        this.acctGroupId = acctGroupId;
        this.tranTypeCd = tranTypeCd;
        this.tranCatCd = tranCatCd;
    }

    public String getAcctGroupId() {
        return acctGroupId;
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
        if (!(o instanceof DisclosureGroupId that)) return false;
        return Objects.equals(acctGroupId, that.acctGroupId)
            && Objects.equals(tranTypeCd, that.tranTypeCd)
            && Objects.equals(tranCatCd, that.tranCatCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acctGroupId, tranTypeCd, tranCatCd);
    }
}
