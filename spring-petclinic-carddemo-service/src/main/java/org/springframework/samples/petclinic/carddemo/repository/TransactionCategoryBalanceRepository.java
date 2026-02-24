package org.springframework.samples.petclinic.carddemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalance;
import org.springframework.samples.petclinic.carddemo.model.TransactionCategoryBalanceId;

import java.util.List;

public interface TransactionCategoryBalanceRepository
    extends JpaRepository<TransactionCategoryBalance, TransactionCategoryBalanceId> {

    List<TransactionCategoryBalance> findByAcctId(Long acctId);
}
