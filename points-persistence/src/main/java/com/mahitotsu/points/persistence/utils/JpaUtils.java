package com.mahitotsu.points.persistence.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

public class JpaUtils {

    public static Map<Class<?>, String> getEntityNames(final EntityManager entityManager) {
        return entityManager == null ? Collections.emptyMap()
                : entityManager.getMetamodel().getEntities().stream()
                        .collect(Collectors.toMap(e -> e.getJavaType(), e -> e.getName()));
    }

    public static Map<String, Class<?>> getEntityTypes(final EntityManager entityManager) {
        return entityManager == null ? Collections.emptyMap()
                : entityManager.getMetamodel().getEntities().stream()
                        .collect(Collectors.toMap(e -> e.getName(), e -> e.getJavaType()));
    }

    public static Optional<String> getEntityName(final Class<?> entityClass) {
        return Optional
                .ofNullable(entityClass == null ? null : AnnotationUtils.findAnnotation(entityClass, Entity.class))
                .map(a -> a.name());
    }

    private JpaUtils() {
    }
}
