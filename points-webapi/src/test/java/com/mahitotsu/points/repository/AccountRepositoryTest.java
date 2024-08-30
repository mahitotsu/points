package com.mahitotsu.points.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.TestBase;

public class AccountRepositoryTest extends TestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testOpenAndClose() {

        final String branchCode = "001";
        final String accountNumber = this.accountRepository.openAccount(branchCode);
        assertTrue(this.accountRepository.isAccountActive(branchCode, accountNumber));

        this.accountRepository.closeAccount(branchCode, accountNumber);
        assertFalse(this.accountRepository.isAccountActive(branchCode, accountNumber));
    }

    @Test
    public void testPointOperations() {

        final String branchCode = "001";
        final String accountNumber = this.accountRepository.openAccount(branchCode);

        int balance = 0;
        for (int i = 0; i < 10; i++) {
            this.accountRepository.addPoints(branchCode, accountNumber, i);
            balance += i;
            assertEquals(balance, this.accountRepository.getPointBalance(branchCode, accountNumber));
        }
        for (int i = 0; i < 10; i++) {
            this.accountRepository.deductPoints(branchCode, accountNumber, i);
            balance -= i;
            assertEquals(balance, this.accountRepository.getPointBalance(branchCode, accountNumber));
        }
    }
}
