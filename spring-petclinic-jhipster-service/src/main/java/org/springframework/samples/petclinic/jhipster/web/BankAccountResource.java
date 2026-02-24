package org.springframework.samples.petclinic.jhipster.web;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.jhipster.model.BankAccount;
import org.springframework.samples.petclinic.jhipster.model.BankAccountRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link BankAccount}.
 */
@RestController
@RequestMapping("/api/bank-accounts")
@Transactional
@Timed("petclinic.jhipster.bank-account")
class BankAccountResource {

    private static final Logger log = LoggerFactory.getLogger(BankAccountResource.class);

    private final BankAccountRepository bankAccountRepository;

    BankAccountResource(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * {@code POST  /bank-accounts} : Create a new bankAccount.
     */
    @PostMapping("")
    public ResponseEntity<BankAccount> createBankAccount(@Valid @RequestBody BankAccount bankAccount) throws URISyntaxException {
        log.debug("REST request to save BankAccount : {}", bankAccount);
        if (bankAccount.getId() != null) {
            throw new BadRequestException("A new bankAccount cannot already have an ID", "bankAccount", "idexists");
        }
        bankAccount = bankAccountRepository.save(bankAccount);
        return ResponseEntity.created(new URI("/api/bank-accounts/" + bankAccount.getId())).body(bankAccount);
    }

    /**
     * {@code PUT  /bank-accounts/:id} : Updates an existing bankAccount.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BankAccount> updateBankAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BankAccount bankAccount
    ) {
        log.debug("REST request to update BankAccount : {}, {}", id, bankAccount);
        if (bankAccount.getId() == null) {
            throw new BadRequestException("Invalid id", "bankAccount", "idnull");
        }
        if (!Objects.equals(id, bankAccount.getId())) {
            throw new BadRequestException("Invalid ID", "bankAccount", "idinvalid");
        }
        if (!bankAccountRepository.existsById(id)) {
            throw new ResourceNotFoundException("BankAccount " + id + " not found");
        }
        bankAccount = bankAccountRepository.save(bankAccount);
        return ResponseEntity.ok().body(bankAccount);
    }

    /**
     * {@code PATCH  /bank-accounts/:id} : Partial updates given fields of an existing bankAccount.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BankAccount> partialUpdateBankAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BankAccount bankAccount
    ) {
        log.debug("REST request to partial update BankAccount partially : {}, {}", id, bankAccount);
        if (bankAccount.getId() == null) {
            throw new BadRequestException("Invalid id", "bankAccount", "idnull");
        }
        if (!Objects.equals(id, bankAccount.getId())) {
            throw new BadRequestException("Invalid ID", "bankAccount", "idinvalid");
        }
        if (!bankAccountRepository.existsById(id)) {
            throw new ResourceNotFoundException("BankAccount " + id + " not found");
        }

        Optional<BankAccount> result = bankAccountRepository
            .findById(bankAccount.getId())
            .map(existingBankAccount -> {
                updateIfPresent(existingBankAccount::setName, bankAccount.getName());
                updateIfPresent(existingBankAccount::setBalance, bankAccount.getBalance());
                return existingBankAccount;
            })
            .map(bankAccountRepository::save);

        return result.map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("BankAccount " + id + " not found"));
    }

    /**
     * {@code GET  /bank-accounts} : get all the bankAccounts.
     */
    @GetMapping("")
    public List<BankAccount> getAllBankAccounts(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all BankAccounts");
        if (eagerload) {
            return bankAccountRepository.findAllWithEagerRelationships();
        } else {
            return bankAccountRepository.findAll();
        }
    }

    /**
     * {@code GET  /bank-accounts/:id} : get the "id" bankAccount.
     */
    @GetMapping("/{id}")
    public BankAccount getBankAccount(@PathVariable("id") Long id) {
        log.debug("REST request to get BankAccount : {}", id);
        return bankAccountRepository.findOneWithEagerRelationships(id)
            .orElseThrow(() -> new ResourceNotFoundException("BankAccount " + id + " not found"));
    }

    /**
     * {@code DELETE  /bank-accounts/:id} : delete the "id" bankAccount.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBankAccount(@PathVariable("id") Long id) {
        log.debug("REST request to delete BankAccount : {}", id);
        bankAccountRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
