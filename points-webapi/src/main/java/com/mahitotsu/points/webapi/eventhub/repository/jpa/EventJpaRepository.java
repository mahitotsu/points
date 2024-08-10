package com.mahitotsu.points.webapi.eventhub.repository.jpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.webapi.eventhub.repository.Event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

@Repository
class EventJpaRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEvents(final String targetEntityName, final UUID targetEntityId,
            final long startTime, final long stopTime, final int maxResult) {
        return this.fetchEvents(targetEntityName, targetEntityId, Collections.emptySet(), startTime, stopTime,
                maxResult);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEvents(final String targetEntityName, final UUID targetEntityId,
            final Class<? extends Event> eventType, final long startTime, final long stopTime, final int maxResult) {
        return this.fetchEvents(targetEntityName, targetEntityId, Collections.singleton(eventType), startTime, stopTime,
                maxResult);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEvents(final String targetEntityName, final UUID targetEntityId,
            final Set<Class<? extends Event>> eventTypes, final long startTime, final long stopTime,
            final int maxResult) {
        if (maxResult <= 0) {
            return Collections.<EventEntity>emptySet().stream();
        }

        final long direction = stopTime - startTime;
        final Map<String, Object> params = new HashMap<>();
        final StringBuffer sqlStatement = new StringBuffer("select e from Event e");

        if (direction >= 0) {
            sqlStatement.append(" where e.eventTime >= :startTime");
            sqlStatement.append(" and e.eventTime < :stopTime");
        } else {
            sqlStatement.append(" where e.eventTime <= :startTime");
            sqlStatement.append(" and e.eventTime > :stopTime");
        }
        params.put("startTime", startTime);
        params.put("stopTime", stopTime);

        if (targetEntityName != null) {
            sqlStatement.append(" and e.targetEntityName = :targetEntityName");
            params.put("targetEntityName", targetEntityName);
        }

        if (targetEntityId != null) {
            sqlStatement.append(" and e.targetEntityId = :targetEntityId");
            params.put("targetEntityId", targetEntityId);
        }

        if (eventTypes != null && eventTypes.isEmpty() == false) {
            sqlStatement.append(" and e.eventType in :eventTypes");
            params.put("eventTypes", eventTypes);
        }

        sqlStatement.append(" order by e.id " + (direction >= 0 ? "asc" : "desc"));

        final TypedQuery<EventEntity> query = this.entityManager.createQuery(sqlStatement.toString(),
                EventEntity.class);
        params.forEach((key, value) -> query.setParameter(key, value));
        query.setMaxResults(maxResult);
        query.setLockMode(LockModeType.NONE);

        return query.getResultStream();
    }

    @Transactional
    public <T extends Event> UUID putEvent(final String targetEntityName, final UUID targetEntityId,
            final Class<T> eventType,
            final T paylaod) {

        final EventEntity event = new EventEntity(targetEntityName, targetEntityId, eventType, paylaod);
        this.entityManager.persist(event);
        return event.getId();
    }
}
