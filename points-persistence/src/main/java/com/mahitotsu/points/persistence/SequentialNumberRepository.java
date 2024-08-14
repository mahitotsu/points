package com.mahitotsu.points.persistence;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SequentialNumberRepository {

    @Autowired
    private DataSource dataSource;

    @Transactional
    public long incrementAndGet(final String sequenceName) {

        final Long value = new JdbcTemplate(this.dataSource).queryForObject("select nextval('" + sequenceName + "')",
                Long.class);
        if (value == null) {
            throw new NullPointerException("The specified sequence returns null.");
        }
        return value.longValue();
    }
}
