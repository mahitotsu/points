package com.mahitotsu.points.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.entity.AccountIdentity;
import com.mahitotsu.points.entity.AccountStatusEvent;
import com.mahitotsu.points.entity.AccountStatusEvent.Status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
@Transactional
public class AccountRepository {

    @Autowired
    private EntityManager entityManager;

    private String nextAccountNumber(final String branchCode) {

        final String lastAccountNumber = this.entityManager.createQuery("""
                SELECT a.accountId.accountNumber
                FROM AccountStatusEvent a
                WHERE a.accountId.branchCode = :branchCode
                  AND a.status = :status
                ORDER BY a.ts desc, a.tx desc, a.id desc
                """, String.class)
                .setParameter("branchCode", branchCode)
                .setParameter("status", AccountStatusEvent.Status.OPENED)
                .setMaxResults(1)
                .getResultStream().findFirst().orElse(null);

        return String.format("%07d", lastAccountNumber == null ? 0 : Integer.parseInt(lastAccountNumber));
    }

    private List<AccountStatusEvent> listAccountStatusEvent(final AccountIdentity accountId,
            final LockModeType lockMode) {

        return this.entityManager.createQuery("""
                SELECT a
                FROM AccountStatusEvent a
                WHERE a.accountId = :accountId
                ORDER BY a.ts desc, a.tx desc, a.id desc
                """, AccountStatusEvent.class)
                .setParameter("accountId", accountId)
                .setLockMode(lockMode)
                .setMaxResults(1)
                .getResultList();
    }

    @Transactional
    public boolean isAccountActive(final String branchCdoe, final String accountNumber) {

        return this.listAccountStatusEvent(new AccountIdentity(branchCdoe, accountNumber), LockModeType.NONE)
                .size() == 1;
    }

    public String openAccount(final String branchCdoe) {

        if (this.listAccountStatusEvent(new AccountIdentity(branchCdoe, "0000000"), LockModeType.PESSIMISTIC_WRITE)
                .size() != 1) {
            throw new IllegalStateException("An unexpected condition has occurred.");
        }

        final String nextAccountNumber = this.nextAccountNumber(branchCdoe);
        final AccountStatusEvent event = new AccountStatusEvent(new AccountIdentity(branchCdoe, nextAccountNumber),
                Status.OPENED);
        this.entityManager.persist(event);

        return nextAccountNumber;
    }

    public void closeAccount(final String branchCdoe, final String accountNumber) {

        final List<AccountStatusEvent> events = this
                .listAccountStatusEvent(new AccountIdentity(branchCdoe, accountNumber), LockModeType.PESSIMISTIC_WRITE);
        if (events.size() != 1) {
            return;
        }

        final AccountStatusEvent event = new AccountStatusEvent(new AccountIdentity(branchCdoe, accountNumber),
                Status.CLOSED);
        this.entityManager.persist(event);
    }

}
