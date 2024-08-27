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

        final Account account = this.accountRepository.openAccount("001");

        assertNotNull(account);
        System.out.println(account);
    }
}
