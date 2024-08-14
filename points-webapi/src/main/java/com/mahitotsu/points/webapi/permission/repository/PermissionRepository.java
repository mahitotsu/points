package com.mahitotsu.points.webapi.permission.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
public class PermissionRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void getPermissionForWrite(final String serviceName) {
        this.getPermission(serviceName, LockModeType.PESSIMISTIC_WRITE);
    }

    @Transactional(readOnly = true)
    public void getPermissionForRead(final String serviceName) {
        this.getPermission(serviceName, LockModeType.PESSIMISTIC_READ);
    }

    @Transactional(readOnly = true)
    public boolean existsPermission(final String serviceName) {
        try {
            return this.getPermission(serviceName, LockModeType.NONE) != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private PermissionEntity getPermission(final String serviceName, final LockModeType lockMode) {
        if (serviceName == null) {
            throw new IllegalArgumentException("The serviceName must not be null.");
        }

        final List<PermissionEntity> permissionList = this.entityManager
                .createNamedQuery(PermissionEntity.GET_BY_SERVICE, PermissionEntity.class)
                .setParameter("serviceName", serviceName)
                .setLockMode(lockMode)
                .setMaxResults(2)
                .getResultList();
        if (permissionList.size() != 1) {
            throw new IllegalStateException("The illegal state is detected.");
        }
        return permissionList.get(0);
    }

    @Transactional
    public boolean registerPermission(final String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("The serviceName must not be null.");
        }

        if (this.existsPermission(serviceName)) {
            return false;
        }

        final PermissionEntity permission = new PermissionEntity(serviceName);
        this.entityManager.persist(permission);
        return true;
    }
}
