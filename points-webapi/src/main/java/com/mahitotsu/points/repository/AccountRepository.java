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

        final String nextNumbrer = "1234567";
        final AccountStatus status = new AccountStatus(branchCode, nextNumbrer, Status.OPENED);
        this.entityManager.persist(status);
        System.out.println(status.getId());

        return nextNumbrer;
    }
}
