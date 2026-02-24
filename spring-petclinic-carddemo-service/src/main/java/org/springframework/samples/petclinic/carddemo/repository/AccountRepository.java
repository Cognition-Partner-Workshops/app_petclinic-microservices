package org.springframework.samples.petclinic.carddemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.carddemo.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
