package com.mahitotsu.points.persistence.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.persistence.account.AccountStatusChangeEventEntity.Payload;
import com.mahitotsu.points.persistence.account.AccountStatusChangeEventEntity.Status;
import com.mahitotsu.points.persistence.eventstore.EventRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
public class AccountRepository {

    private static final String BRANCH_ACCOUNT_NUMBER = "0000000";

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EntityManager entityManager;

    private AccountEntity lockAccount(final String branchCode, final String accountNumber,
            final LockModeType lockMode) {
        final List<AccountEntity> accounts = this.entityManager.createQuery(
                "select a from Account a where a.branchCode = :branchCode and a.accountNumber = :accountNumber",
                AccountEntity.class)
                .setParameter("branchCode", branchCode)
                .setParameter("accountNumber", accountNumber)
                .setLockMode(lockMode)
                .setMaxResults(1)
                .getResultList();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Transactional
    public AccountEntity openAccount(final String branchCode) {

        if (this.lockAccount(branchCode, BRANCH_ACCOUNT_NUMBER, LockModeType.PESSIMISTIC_WRITE) == null) {
            throw new IllegalArgumentException("The specified branch is not fouond.");
        }

        final List<String> maxAccountNumber = this.entityManager.createQuery(
                "select a.accountNumber from Account a where a.branchCode = :branchCode order by a.accountNumber desc",
                String.class)
                .setParameter("branchCode", branchCode)
                .setLockMode(LockModeType.PESSIMISTIC_READ)
                .setMaxResults(1)
                .getResultList();

        final String accountNumber = String.format("%07d", Integer.parseInt(maxAccountNumber.get(0)) + 1);
        final AccountEntity account = new AccountEntity(branchCode, accountNumber);
        this.entityManager.persist(account);

        final AccountStatusChangeEventEntity statusChangedEvent = new AccountStatusChangeEventEntity();
        statusChangedEvent.init(new Payload(Status.OPEND), account.getEntityName(), account.getEntityId());
        this.entityManager.persist(statusChangedEvent);

        return account;
    }

    @Transactional
    public void closeAccount(final String branchCode, final String accountNumber) {

        final AccountEntity account = this.lockAccount(branchCode, accountNumber, LockModeType.PESSIMISTIC_WRITE);
        if (account == null) {
            return;
        }

        final AccountStatusChangeEventEntity statusChangedEvent = new AccountStatusChangeEventEntity();
        statusChangedEvent.init(new Payload(Status.CLOSED), account.getEntityName(), account.getEntityId());
        this.entityManager.persist(statusChangedEvent);
    }

    @Transactional(readOnly = true)
    public boolean isAccountAvailable(final String branchCode, final String accountNumber) {

        // check only existence.
        // because you can not get a shared lock with readonly transaction.
        final AccountEntity account = this.lockAccount(branchCode, accountNumber, LockModeType.NONE);
        if (account == null) {
            return false;
        }

        final AccountStatusChangeEventEntity event = this.eventRepository.findLastEvent(
                this.eventRepository.getEventType(AccountStatusChangeEventEntity.class),
                account.getEntityName(), account.getEntityId())
                .map(e -> AccountStatusChangeEventEntity.class.cast(e)).orElse(null);
        return event != null && event.getEventPayload().getStatus() == Status.OPEND;
    }
}
