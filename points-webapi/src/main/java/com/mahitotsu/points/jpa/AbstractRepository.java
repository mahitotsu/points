package com.mahitotsu.points.jpa;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

public class AbstractRepository {

    public static interface ResultStreamHandler<T, R> {
        R doWith(EntityManager entityManager, Stream<T> resultStream);
    }

    public static interface SingleResultHandler<T, R> {
        R doWith(EntityManager entityManager, T singleResult);
    }

    @Autowired
    private EntityManager entityManager;

    protected <T, R> R processResultList(final String query,
            final Class<T> requiredType,
            final LockModeType lockMode,
            final Map<String, Object> parameters,
            final ResultStreamHandler<T, R> handler) {

        final TypedQuery<T> tq = this.entityManager.createQuery(query, requiredType)
                .setMaxResults(1)
                .setLockMode(lockMode);
        if (parameters != null) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                tq.setParameter(parameter.getKey(), parameter.getValue());
            }
        }

        return handler != null ? handler.doWith(this.entityManager, tq.getResultStream()) : null;
    }

    protected <T, R> R processSingleResult(final String query, final Class<T> requiredType, final LockModeType lockMode,
            final Map<String, Object> parameters, final SingleResultHandler<T, R> handler) {

        return this.processResultList(query, requiredType, lockMode, parameters, (entityManager, resultStream) -> {
            return resultStream.findFirst()
                    .map(result -> handler != null ? handler.doWith(entityManager, result) : null)
                    .orElse(null);
        });
    }
}
