package com.mahitotsu.points.jpa;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

public abstract class EntityRepository {

    @Autowired
    private EntityManager entityManager;

    protected <E extends EntityBase> E persist(final E entity) {
        this.entityManager.persist(entity);
        return entity;
    }

    protected <E extends EntityBase, R> R processSingleEntity(final String queryString,
            final Class<E> entityType, final Map<String, Object> params, final LockModeType lockMode,
            final Function<E, R> handler) {

        return this.processEntityStream(queryString, entityType, params, lockMode, 1,
                (s -> handler != null ? s.findFirst().map(handler).orElse(null) : null));
    }

    protected <E extends EntityBase, R> Stream<R> processAllEntities(final String queryString,
            final Class<E> entityType, final Map<String, Object> params, final LockModeType lockMode,
            final Integer maxResult, final Function<E, R> handler) {

        return this.processEntityStream(queryString, entityType, params, lockMode, maxResult,
                (s -> handler != null ? s.map(handler) : null));
    }

    protected <E extends EntityBase, R> R processEntityStream(final String queryString, final Class<E> entityType,
            final Map<String, Object> params, final LockModeType lockMode, final Integer maxResult,
            final Function<Stream<E>, R> handler) {

        final TypedQuery<E> query = this.entityManager.createQuery(queryString, entityType);
        if (params != null) {
            params.entrySet().forEach(e -> query.setParameter(e.getKey(), e.getValue()));
        }
        if (lockMode != null) {
            query.setLockMode(this.adjustLockMode(lockMode));
        }
        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }

        return handler != null ? handler.apply(query.getResultStream()) : null;
    }

    private LockModeType adjustLockMode(final LockModeType lockMode) {

        if (lockMode == null) {
            return null;
        }

        final boolean isRO = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        switch (lockMode) {
            case PESSIMISTIC_READ: // within readonly transaction, you can not get shared lock.
                return isRO ? LockModeType.NONE : lockMode;
            default:
                return lockMode;
        }
    }
}
