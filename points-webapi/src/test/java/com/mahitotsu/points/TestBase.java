package com.mahitotsu.points;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.mahitotsu.points.entity.EntityBase;

import jakarta.persistence.EntityManager;

@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TestBase {

    @Autowired
    private ListableBeanFactory beanFactory;

    @Test
    public void test() throws InterruptedException, ExecutionException {

        final ExecutorService es = Executors.newFixedThreadPool(10);
        final BlockingQueue<Future<Integer>> queue = new LinkedBlockingQueue<>();

        for (int i = 0; i < 10; i++) {
            queue.add(es.submit(() -> {
                final TransactionTemplate txOps = new TransactionTemplate(
                        this.beanFactory.getBean(PlatformTransactionManager.class));
                for (int j = 0; j < 9; j++) {
                    final EntityManager entityManager = this.beanFactory.getBean(EntityManager.class);
                    final UUID id = txOps.execute(tx -> {
                        final EntityBase eb = new EntityBase();
                        entityManager.persist(eb);
                        return eb.getId();
                    });
                    txOps.executeWithoutResult(tx -> {
                        final EntityBase eb = entityManager.find(EntityBase.class, id);
                        System.out.println(eb);
                    });
                }
                return 1;
            }));
        }

        while (queue.isEmpty() == false) {
            queue.take().get();
        }
        es.shutdown();
    }
}