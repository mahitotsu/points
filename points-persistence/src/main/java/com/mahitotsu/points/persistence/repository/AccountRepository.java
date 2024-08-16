package com.mahitotsu.points.persistence.repository;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccountRepository {

    public static final String ACCOUNT_NUMBER_SEQ = "account_number_seq";

    @Autowired
    private SequentialNumberRepository sequentialNumberRepository;

    @Autowired
    private DataSource dataSource;

    public enum AccountStatus {
        OPENED, CLOSED,
    }

    @Transactional
    public String openAccount() {

        final long nextVal = this.sequentialNumberRepository.incrementAndGet(ACCOUNT_NUMBER_SEQ);
        final String accountNumber = String.format("%010d", nextVal);

        new NamedParameterJdbcTemplate(this.dataSource).update("""
                INSERT INTO account (account_number, account_status)
                VALUES (:accountNumber, :accountStatus)
                """,
                new MapSqlParameterSource()
                        .addValue("accountNumber", accountNumber)
                        .addValue("accountStatus", AccountStatus.OPENED.ordinal()));
        return accountNumber;
    }

    @Transactional
    public void closeAccount(final String accountNumber) {
        if (accountNumber == null) {
            return;
        }

        new NamedParameterJdbcTemplate(this.dataSource).update("""
                UPDATE account
                SET account_status = :accountStatus
                WHERE account_number = :accountNumber
                """,
                new MapSqlParameterSource()
                        .addValue("accountNumber", accountNumber)
                        .addValue("accountStatus", AccountStatus.CLOSED.ordinal()));
    }

    @Transactional(readOnly = true)
    public boolean isAccountAvailable(final String accountNumber) {
        if (accountNumber == null) {
            return false;
        }

        final Integer statusOrdinal = new NamedParameterJdbcTemplate(this.dataSource).queryForObject("""
                SELECT a.account_status
                FROM account a
                WHERE account_number = :accountNumber
                """,
                new MapSqlParameterSource()
                        .addValue("accountNumber", accountNumber),
                new SingleColumnRowMapper<>(Integer.class));
        return statusOrdinal != null && statusOrdinal.intValue() == AccountStatus.OPENED.ordinal();
    }
}
