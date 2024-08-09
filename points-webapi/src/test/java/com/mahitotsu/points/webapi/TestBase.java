package com.mahitotsu.points.webapi;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class TestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private PlatformTransactionManager txMgr;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = this.port;
    }

    @Transactional(readOnly = true)
    public <T> T doInROTx(final Supplier<T> task) {
        final TransactionTemplate txOp = new TransactionTemplate(this.txMgr);
        txOp.setReadOnly(true);
        return txOp.execute((tx) -> task.get());
    }
}
