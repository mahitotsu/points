package com.mahitotsu.points.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.jpa.open-in-vew=false",
        "spring.jpa.show-sql=false",
        "spring.jpa.generate-ddl=false"
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TestBase {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    public <T> T doWithTransaction(final TransactionCallback<T> action) {
        final TransactionAttribute txDef = new DefaultTransactionAttribute();
        final TransactionOperations txOps = new TransactionTemplate(this.platformTransactionManager, txDef);
        return txOps.execute(action);
    }
}
