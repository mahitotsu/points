package com.mahitotsu.points.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.TestBase;

public class EntityRepositoryTest extends TestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void test() {

        final String branchCode = "001";
        final String accountNumber = this.accountRepository.openAccount(branchCode);
        assertNotNull(accountNumber);
    }
}
