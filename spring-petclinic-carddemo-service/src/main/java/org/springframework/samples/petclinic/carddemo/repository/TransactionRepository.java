package org.springframework.samples.petclinic.carddemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.carddemo.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
