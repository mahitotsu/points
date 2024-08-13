package com.mahitotsu.points.webapi.account.repository;

import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.webapi.account.repository.AccountEntity.Status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
public class AccountRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public UUID create(final Consumer<AccountEntity> initializer) {

        final AccountEntity entity = new AccountEntity();
        entity.setStatus(Status.OPENED);

        if (initializer != null) {
            initializer.accept(entity);
        }

        this.entityManager.persist(entity);
        return entity.getId();
    }

    @Transactional
    public void update(final UUID id, final Consumer<AccountEntity> mutator) {

        if (id == null || mutator == null) {
            return;
        }

        final AccountEntity entity = this.entityManager.find(AccountEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (entity == null) {
            return;
        }
        mutator.accept(entity);
    }

    @Transactional(readOnly = true)
    public AccountEntity read(final UUID id) {

        return this.entityManager.find(AccountEntity.class, id, LockModeType.PESSIMISTIC_READ);
    }
}
