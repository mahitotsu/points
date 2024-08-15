package com.mahitotsu.points.persistence.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.persistence.entity.AccountEntity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.LockModeType;

@Repository
public class AccountRepository extends AbstractRepository<AccountEntity, String> {

    private static final String ACCOUNT_SEQ_NAME = "AccountSeqName";

    public AccountRepository() {
        super(AccountEntity.class);
    }

    @Autowired
    private SequentialNumberRepository sequentialNumberJpaRepository;

    @PostConstruct
    public void setup() {
        this.sequentialNumberJpaRepository.registerSequentialNumberIfNotExists(ACCOUNT_SEQ_NAME, 0);
    }

    @Transactional
    public String openAccount() {

        final String number = String.format("%010d",
                this.sequentialNumberJpaRepository.increaseAndGet(AccountRepository.ACCOUNT_SEQ_NAME));

        final AccountEntity account = new AccountEntity(number);
        this.persist(account);

        return account.getNumber();
    }

    private AccountEntity getAccountByNumber(final String accountNumber, final LockModeType lockMode) {
        if (accountNumber == null) {
            return null;
        }

        return this.getById(accountNumber, lockMode).orElse(null);
    }

    @Transactional
    public boolean isAccountAvailable(final String accountNumber) {

        final AccountEntity account = this.getAccountByNumber(accountNumber, LockModeType.PESSIMISTIC_READ);
        return account != null && account.isAvailable();
    }

    @Transactional
    public void closeAccount(final String accountNumber) {

        final AccountEntity account = this.getAccountByNumber(accountNumber, LockModeType.PESSIMISTIC_WRITE);
        if (account == null) {
            return;
        }

        account.close();
    }
}
