package com.mahitotsu.points.webapi.account.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import com.mahitotsu.points.webapi.account.repository.AccountEntity.Status;

@DataJpaTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = { Repository.class })
})
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testCRUDAccount() {

        final UUID id = this.accountRepository.create((account) -> {
            assertNotNull(account);
            assertEquals(Status.OPENED, account.getStatus());
            assertEquals(0, account.getPoints());
            account.setPoints(100);
            assertEquals(100, account.getPoints());
        });
        assertNotNull(id);

        final AccountEntity a1 = this.accountRepository.read(id);
        assertNotNull(a1);
        assertEquals(Status.OPENED, a1.getStatus());
        assertEquals(100, a1.getPoints());

        this.accountRepository.update(id, (account) -> {
            assertNotNull(account);
            assertEquals(100, account.getPoints());
            account.setPoints(200);
        });

        final AccountEntity a2 = this.accountRepository.read(id);
        assertNotNull(a2);
        assertEquals(AccountEntity.Status.OPENED, a2.getStatus());
        assertEquals(200, a2.getPoints());
    }
}