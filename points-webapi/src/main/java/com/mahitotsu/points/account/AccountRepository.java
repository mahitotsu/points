package com.mahitotsu.points.account;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.account.Account.Status;
import com.mahitotsu.points.entity.EntityRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;

@Repository
public class AccountRepository {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRepository entityRepository;

    private Account getAccount(final String branchCode, final String accountNumber) {
        try {
            return this.entityManager.createQuery("""
                    SELECT a FROM Account a
                     WHERE a.branchCode = :branchCode
                       AND a.accountNumber = :accountNumber
                    """, Account.class)
                    .setParameter("branchCode", branchCode)
                    .setParameter("accountNumber", accountNumber)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private Account getBranchAccount(final String branchCode) {
        return this.getAccount(branchCode, "0000000");
    }

    private String getNextAccountNumber(final String branchCode) {
        try {
            final String lastAccountNumber = this.entityManager.createQuery("""
                    SELECT a.accountNumber FROM Account a
                     WHERE a.branchCode = :branchCode
                     ORDER BY a.accountNumber desc
                    """, String.class)
                    .setParameter("branchCode", branchCode)
                    .setMaxResults(1)
                    .getResultList().get(0);
            final String nextAccountNumber = String.format("%07d",
                    Integer.parseInt(lastAccountNumber) + 1);
            return nextAccountNumber;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Transactional
    public String openAccount(final String branchCode) {

        final Account account = this.getBranchAccount(branchCode);
        if (account == null) {
            throw new IllegalArgumentException("The specified branch code is not valid.");
        }

        if (!this.entityRepository.lockEntity(account.getId(), LockModeType.PESSIMISTIC_WRITE)) {
            throw new IllegalStateException("An invalid state has occurred.");
        }
        final String nextAccountNumber = this.getNextAccountNumber(branchCode);
        this.entityManager.persist(new Account(branchCode, nextAccountNumber, Status.OPENED));

        return nextAccountNumber;
    }

    @Transactional
    public void closeAccount(final String branchCode, final String accountNumber) {

        final Account account = this.getAccount(branchCode, accountNumber);
        if (account == null) {
            return;
        }

        if (!this.entityRepository.lockEntity(account.getId(), LockModeType.PESSIMISTIC_WRITE)) {
            throw new IllegalStateException("An invalid state has occurred.");
        }
        account.setStatus(Status.CLOSED);
    }

    @Transactional(readOnly = true)
    public boolean isAccountActive(final String branchCode, final String accountNumber) {

        final Account account = this.getAccount(branchCode, accountNumber);
        return account != null && account.getStatus() == Status.OPENED;
    }
}
