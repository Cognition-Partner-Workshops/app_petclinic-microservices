package org.springframework.samples.petclinic.carddemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.carddemo.model.CardXref;

import java.util.Optional;

public interface CardXrefRepository extends JpaRepository<CardXref, String> {

    Optional<CardXref> findFirstByAcctId(Long acctId);
}
