package com.mahitotsu.points.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountRepositoryTest extends RepositoryTestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void openAndCloseAccount() {

        final String accountNumber = this.accountRepository.openAccount();
        assertNotNull(accountNumber);
        assertTrue(this.accountRepository.isAccountAvailable(accountNumber));

        this.accountRepository.closeAccount(accountNumber);
        assertFalse(this.accountRepository.isAccountAvailable(accountNumber));
    }
}
