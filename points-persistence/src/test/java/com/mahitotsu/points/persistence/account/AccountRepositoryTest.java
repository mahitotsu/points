package com.mahitotsu.points.persistence.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.persistence.TestBase;

public class AccountRepositoryTest extends TestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testAccountLifecycle() {

        final String branchCode = "001";

        final AccountEntity account = this.accountRepository.openAccount(branchCode);
        assertNotNull(account);
        assertEquals(branchCode, account.getBranchCode());
        assertNotNull(account.getAccountNumber());

        final String accountNumber = account.getAccountNumber();
        assertTrue(this.accountRepository.isAccountAvailable(branchCode, accountNumber));

        this.accountRepository.closeAccount(branchCode, accountNumber);
        assertFalse(this.accountRepository.isAccountAvailable(branchCode, accountNumber));
    }
}
