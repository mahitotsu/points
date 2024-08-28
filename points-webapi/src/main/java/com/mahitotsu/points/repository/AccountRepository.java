package com.mahitotsu.points.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.entity.AccountStatus;
import com.mahitotsu.points.entity.AccountStatus.Status;

import jakarta.persistence.EntityManager;

@Repository
@Transactional
public class AccountRepository {

    @Autowired
    private EntityManager entityManager;

    public String openAccount(final String branchCode) {

        final AccountStatus accountStatus = new AccountStatus("001", "1234567", Status.OPENED);
        this.entityManager.persist(accountStatus);
        this.entityManager.refresh(accountStatus);

        return accountStatus.getAccountNumber();
    }
}
