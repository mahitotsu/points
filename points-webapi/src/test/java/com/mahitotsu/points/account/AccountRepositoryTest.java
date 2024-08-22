package com.mahitotsu.points.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.mahitotsu.points.TestBase;
import com.mahitotsu.points.event.Event;

import jakarta.persistence.EntityManager;

public class AccountRepositoryTest extends TestBase {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void testOpenAndCloseAccount() {

        for (int i = 0; i < 100; i++) {
            final Event event = new Event();
            new TransactionTemplate(this.transactionManager).execute((tx) -> {
                this.entityManager.persist(event);
                return event;
            });
            System.out.println(event.toString());
        }

    }
}
