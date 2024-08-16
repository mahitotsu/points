package com.mahitotsu.points.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql({ "/test-queries/010-create-account-schemas.sql" })
public class AccountRepositoryTest extends RepositoryTestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testOpenAndCloseAccount() {

        final String accountNumber = this.accountRepository.openAccount();
        assertNotNull(accountNumber);
        assertTrue(this.accountRepository.isAccountAvailable(accountNumber));

        this.accountRepository.closeAccount(accountNumber);
        assertFalse(this.accountRepository.isAccountAvailable(accountNumber));
    }

    @Test
    public void testNullAccountNumber() {

        final String accountNumber = null;
        assertNull(accountNumber);

        assertFalse(this.accountRepository.isAccountAvailable(accountNumber));
        assertDoesNotThrow(() -> this.accountRepository.closeAccount(accountNumber));
    }
}
