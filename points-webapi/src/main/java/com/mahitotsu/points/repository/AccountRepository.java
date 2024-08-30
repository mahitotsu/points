package com.mahitotsu.points.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.entity.AccountIdentity;
import com.mahitotsu.points.entity.AccountStatusEvent;
import com.mahitotsu.points.entity.AccountStatusEvent.Status;
import com.mahitotsu.points.entity.PointChangedEvent;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
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
                .setMaxResults(2)
                .getResultStream().findFirst().orElse(null);
        return String.format("%07d", (lastAccountNumber == null ? 0 : Integer.parseInt(lastAccountNumber)) + 1);
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
                .setMaxResults(2)
                .getResultList();
    }

    private long getPointBalance(final AccountIdentity accountId) {

        return this.entityManager.createQuery("""
                SELECT sum(c.amount)
                FROM PointChangedEvent c
                WHERE c.accountId = :accountId
                """, Long.class)
                .setParameter("accountId", accountId)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Transactional(readOnly = true)
    public boolean isAccountActive(final String branchCdoe, final String accountNumber) {

        final List<AccountStatusEvent> events = this
                .listAccountStatusEvent(new AccountIdentity(branchCdoe, accountNumber), LockModeType.NONE);
        return events.size() == 1 && events.get(0).getStatus() == Status.OPENED;
    }

    @Transactional
    public String openAccount(final String branchCode) {

        if (this.listAccountStatusEvent(new AccountIdentity(branchCode, "0000000"), LockModeType.PESSIMISTIC_WRITE)
                .size() != 1) {
            throw new IllegalStateException("An unexpected condition has occurred.");
        }

        final String nextAccountNumber = this.nextAccountNumber(branchCode);
        final AccountStatusEvent event = new AccountStatusEvent(new AccountIdentity(branchCode, nextAccountNumber),
                Status.OPENED);
        this.entityManager.persist(event);

        return nextAccountNumber;
    }

    @Transactional
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

    @Transactional
    public void addPoints(final String branchCode, final String accountNumber, final int amount) {

        if (amount < 0) {
            throw new IllegalArgumentException("The point amount must be a positive number.");
        }

        final List<AccountStatusEvent> events = this
                .listAccountStatusEvent(new AccountIdentity(branchCode, accountNumber), LockModeType.PESSIMISTIC_READ);
        if (events.size() != 1) {
            throw new IllegalStateException("An unexpected condition has occurred.");
        }

        final PointChangedEvent event = new PointChangedEvent(new AccountIdentity(branchCode, accountNumber), amount);
        this.entityManager.persist(event);
    }

    @Transactional
    public long deductPoints(final String branchCode, final String accountNumber, final int amount) {

        if (amount < 0) {
            throw new IllegalArgumentException("The point amount must be a positive number.");
        }

        final List<AccountStatusEvent> events = this
                .listAccountStatusEvent(new AccountIdentity(branchCode, accountNumber), LockModeType.PESSIMISTIC_WRITE);
        if (events.size() != 1) {
            throw new IllegalStateException("An unexpected condition has occurred.");
        }

        final AccountIdentity accountId = new AccountIdentity(branchCode, accountNumber);
        final long balance = this.getPointBalance(accountId);
        if (balance < amount) {
            throw new IllegalStateException("The current balance is insufficient.");
        }

        final PointChangedEvent event = new PointChangedEvent(accountId, 0 - amount);
        this.entityManager.persist(event);

        return balance - amount;
    }

    @Transactional(readOnly = true)
    public long getPointBalance(final String branchCode, final String accountNumber) {

        if (this.isAccountActive(branchCode, accountNumber) == false) {
            throw new IllegalStateException("An unexpected condition has occurred.");
        }

        return this.getPointBalance(new AccountIdentity(branchCode, accountNumber));
    }
}
