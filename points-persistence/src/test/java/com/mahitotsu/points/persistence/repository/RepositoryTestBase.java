package com.mahitotsu.points.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@JdbcTest(includeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = { Configuration.class }),
        @Filter(type = FilterType.ANNOTATION, classes = { Repository.class }),
}, properties = {
        "logging.level.org.springframework.jdbc.core.JdbcTemplate=debug",
        "logging.level.org.springframework.jdbc.core.StatementCreatorUtils=trace",
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RepositoryTestBase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    public void printSplitLine(final TestInfo testInfo) {
        this.logger.info("========== [ " + testInfo.getDisplayName() + " ] ==========");
    }
}
