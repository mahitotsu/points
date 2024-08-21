package com.mahitotsu.points.account;

import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.jpa.AbstractRepository;

import jakarta.persistence.LockModeType;

@Repository
public class AccountRepository extends AbstractRepository {

    @Transactional
    public Account openAccount(final String branchCode) {

        return this.<Account, Account>processSingleResult("""
                SELECT a
                FROM Account a
                WHERE a.branchCode = :branchCode
                ORDER BY a.accountNumber desc
                 """,
                Account.class,
                LockModeType.PESSIMISTIC_WRITE,
                Map.ofEntries(
                        Map.entry("branchCode", branchCode) //
                ),

                (entityManager, last) -> {
                    if (last == null) {
                        throw new IllegalStateException("An invalid state occurred.");
                    }

                    final String nextNumber = String.format("%07d", Integer.parseInt(last.getAccountNumber()));
                    final Account account = new Account(branchCode, nextNumber);
                    entityManager.persist(account);

                    return account;
                });
    }

    @Transactional
    public void closeAccount(final String branchCode, final String accountNumber) {

        this.<Account, Account>processSingleResult("""
                SELECT a
                FROM Account a
                WHERE a.branchCode = :branchCode
                  AND a.accountNumber = :accountNumber
                 """,
                Account.class,
                LockModeType.PESSIMISTIC_WRITE,
                Map.ofEntries(
                        Map.entry("branchCode", branchCode),
                        Map.entry("accoutNumber", accountNumber) //
                ),

                (entityManager, account) -> {

                    // TODO
                    return account;
                });
    }

}
