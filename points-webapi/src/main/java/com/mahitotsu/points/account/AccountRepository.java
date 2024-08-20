package com.mahitotsu.points.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.event.EventRepository;
import com.mahitotsu.points.jpa.EntityRepository;

import jakarta.persistence.LockModeType;

@Repository
public class AccountRepository extends EntityRepository {

    @Autowired
    private EventRepository eventRepository;

    private boolean lockByBranch(final String branchCode) {

        return branchCode == null ? false
                : this.processSingleEntity("""
                        SELECT a FROM Account a
                        WHERE a.branchCode = :branchCode
                          AND a.accountNumber = '0000000'
                        """, Account.class,
                        Map.ofEntries(
                                Map.entry("branchCode", branchCode) //
                        ),
                        LockModeType.PESSIMISTIC_WRITE,
                        //
                        (entity -> entity != null));
    }

    private Account lockByAccount(final String branchCode, final String accountNumber, final LockModeType lockMode) {

        return branchCode == null ? null
                : this.processSingleEntity("""
                        SELECT a FROM Account a
                        WHERE a.branchCode = :branchCode
                          AND a.accountNumber = :accountNumber
                        """, Account.class,
                        Map.ofEntries(
                                Map.entry("branchCode", branchCode),
                                Map.entry("accountNumber", accountNumber) //
                        ),
                        lockMode,
                        //
                        (entity -> entity));
    }

    @Transactional
    public Account openAccount(final String branchCode) {

        if (this.lockByBranch(branchCode) == false) {
            throw new IllegalStateException("Unable to process the request due to unfulfilled requirements.");
        }

        final String nextAccountNumber = this.processSingleEntity("""
                SELECT a FROM Account a
                WHERE a.branchCode = :branchCode
                ORDER BY a.accountNumber desc
                """, Account.class,
                Map.ofEntries(
                        Map.entry("branchCode", branchCode) //
                ),
                LockModeType.PESSIMISTIC_READ,
                //
                (account -> String.format("%07d", Integer.parseInt(account.getAccountNumber()) + 1)));

        final Account account = this.persist(new Account(branchCode, nextAccountNumber));
        this.persist(new AccountStatusChangeEvent(account, AccountStatusChangeEvent.Status.OPENED));

        return account;
    }

    @Transactional
    public void closeAccount(final String branchCode, final String accountNumber) {

        final Account account = this.lockByAccount(branchCode, accountNumber, LockModeType.PESSIMISTIC_WRITE);
        if (account == null) {
            throw new IllegalStateException("Unable to process the request due to unfulfilled requirements.");
        }

        this.persist(new AccountStatusChangeEvent(account, AccountStatusChangeEvent.Status.CLOSED));
    }

    @Transactional(readOnly = true)
    public boolean isAccountAvailable(final String branchCode, final String accountNumber) {

        final Account account = this.lockByAccount(branchCode, accountNumber, LockModeType.PESSIMISTIC_READ);
        final AccountStatusChangeEvent event = this.eventRepository.findLastEvent(account,
                AccountStatusChangeEvent.class);

        return event != null && event.getPayload().getStatus() == AccountStatusChangeEvent.Status.OPENED;
    }
}
