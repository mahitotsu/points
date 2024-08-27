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

    private Account findLastAccount(final String branchCode) {
        try {
            return this.entityManager.createQuery("""
                    SELECT a FROM Account a
                     WHERE a.branchCode = :branchCode
                     ORDER BY a.accountNumber desc
                    """, Account.class)
                    .setParameter("branchCode", branchCode)
                    .setMaxResults(1)
                    .getResultList().get(0);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Transactional
    public Account openAccount(final String branchCode) {

        final Account branchAccount = this.getBranchAccount(branchCode);
        if (!this.entityRepository.lockEntity(branchAccount.getId(), LockModeType.PESSIMISTIC_WRITE)) {
            throw new IllegalStateException("An invalid state has occurred.");
        }

        final Account lastAccount = this.findLastAccount(branchCode);
        final String nextAccountNumber = String.format("%07d", Integer.parseInt(lastAccount.getAccountNumber()) + 1);

        final Account newAccount = new Account(branchCode, nextAccountNumber, Status.OPENED);
        this.entityManager.persist(newAccount);
        this.entityManager.refresh(newAccount);
        return newAccount;
    }
}
