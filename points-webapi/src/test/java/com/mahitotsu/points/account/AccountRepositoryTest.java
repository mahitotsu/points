package com.mahitotsu.points.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.TestBase;

public class AccountRepositoryTest extends TestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testOpenAndCloseAccount() {

        final String branchCode = "001";
        final String accountNumber = this.accountRepository.openAccount(branchCode);

        assertNotNull(accountNumber);
        assertTrue(this.accountRepository.isAccountActive(branchCode, accountNumber));

        this.accountRepository.closeAccount(branchCode, accountNumber);
        assertFalse(this.accountRepository.isAccountActive(branchCode, accountNumber));
    }
}
