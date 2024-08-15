package com.mahitotsu.points.persistence.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;

public abstract class AbstractRepository<T, I> {

    protected AbstractRepository(final Class<T> entityType) {
        this.entityType = entityType;
    }

    private Class<T> entityType;

    @Autowired
    private EntityManager entityManager;

    protected void persist(final Object entity) {
        this.entityManager.persist(entity);
    }

    protected Optional<T> getById(final I id, final LockModeType lockMode) {

        final T entity = this.entityManager.find(this.entityType, id, lockMode);
        return Optional.ofNullable(entity);
    }

    protected Optional<T> getSingleResult(final String queryName, final Map<String, Object> params,
            final LockModeType lockMode) {
        if (queryName == null) {
            throw new IllegalArgumentException("The query name is required arguments.");
        }

        final TypedQuery<T> query = this.entityManager
                .createNamedQuery(queryName, this.entityType)
                .setLockMode(lockMode != null ? lockMode : LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1);
        if (params != null) {
            params.forEach((k, v) -> query.setParameter(k, v));
        }

        try {
            final T result = query.getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("The illgal state has occured.", e);
        }
    }
}
