package com.mahitotsu.points.webapi.utils;

import java.util.Map;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class EntityManagerUtils {

    public static <T> Stream<T> getEntityStream(final EntityManager entityManager, final Class<T> entityType,
            final String queryName,
            final Map<String, Object> params,
            final int maxResult) {

        final TypedQuery<T> query = entityManager.createNamedQuery(queryName, entityType);
        params.forEach((key, value) -> query.setParameter(key, value));
        query.setMaxResults(maxResult > 0 ? maxResult : Integer.MAX_VALUE);
        return query.getResultStream();
    }

    private EntityManagerUtils() {
    }
}
