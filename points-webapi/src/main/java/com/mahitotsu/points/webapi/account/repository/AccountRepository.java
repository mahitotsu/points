package com.mahitotsu.points.webapi.account.repository;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.webapi.account.repository.AccountEntity.Status;
import com.mahitotsu.points.webapi.permission.repository.PermissionRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
public class AccountRepository {

    private static final String SERVICE_NAME = "account";

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EntityManager entityManager;

    @PostConstruct
    public void setup() {
        this.permissionRepository.registerPermission(SERVICE_NAME);
    }

    @Transactional
    public UUID create(final Consumer<AccountEntity> initializer) {
        this.permissionRepository.getPermissionForWrite(SERVICE_NAME);

        final List<String> lastAccountNumber = this.entityManager
                .createNamedQuery(AccountEntity.GET_LAST_ACCOUNT_NUMBER, String.class)
                .setMaxResults(1)
                .getResultList();
        final String accountNumber = String.format("%010d",
                lastAccountNumber.isEmpty() ? 0 : Integer.parseInt(lastAccountNumber.get(0)) + 1);

        final AccountEntity entity = new AccountEntity(accountNumber);
        entity.setStatus(Status.OPENED);
        entity.setPoints(0);

        if (initializer != null) {
            initializer.accept(entity);
        }

        this.entityManager.persist(entity);
        return entity.getId();
    }

    @Transactional
    public void update(final UUID id, final Consumer<AccountEntity> mutator) {
        this.permissionRepository.getPermissionForWrite(SERVICE_NAME);

        if (id == null || mutator == null) {
            return;
        }

        final AccountEntity entity = this.entityManager.find(AccountEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        mutator.accept(entity);
    }

    @Transactional(readOnly = true)
    public AccountEntity read(final UUID id) {

        return this.entityManager.find(AccountEntity.class, id, LockModeType.PESSIMISTIC_READ);
    }
}
