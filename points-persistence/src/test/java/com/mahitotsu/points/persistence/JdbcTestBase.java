package com.mahitotsu.points.persistence;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest(includeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = { Repository.class, Configuration.class })
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/test-schema.sql")
public class JdbcTestBase {

    @Autowired
    private DataSource dataSource;

    protected NamedParameterJdbcOperations getJdbcOperations() {
        return new NamedParameterJdbcTemplate(this.dataSource);
    }

    protected <T> T getSingleObject(final String sql) {
        return this.getJdbcOperations().queryForObject(sql, Collections.emptyMap(), new SingleColumnRowMapper<T>());
    }
}
