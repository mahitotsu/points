package com.mahitotsu.points.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.TestBase;

public class AccountRepositoryTest extends TestBase {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void test() {

        this.accountRepository.openAccount("001");
    }
}
