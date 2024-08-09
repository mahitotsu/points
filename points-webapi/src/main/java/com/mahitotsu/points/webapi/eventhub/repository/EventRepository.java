package com.mahitotsu.points.webapi.eventhub.repository;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Repository
public class EventRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Stream<EventEntity> fetchEventHistory(final String targetEntityName, final String targetEntityId,
            final String eventType, final long startTime, final long stopTime, final int maxResult) {

        return this.entityManager.createNamedQuery(EventEntity.FETCH_EVENT_HISTORY.NAME, EventEntity.class)
                .setParameter("targetEntityName", targetEntityName)
                .setParameter("targetEntityId", targetEntityId)
                .setParameter("eventType", eventType)
                .setParameter("startTime", startTime)
                .setParameter("stopTime", stopTime)
                .setMaxResults(maxResult > 0 ? maxResult : Integer.MAX_VALUE)
                .getResultStream();
    }

    @Transactional
    public UUID putEvent(final String targetEntityName, final String targetEntityId, final String eventType) {

        final EventEntity event = new EventEntity(targetEntityName, targetEntityId, eventType);
        this.entityManager.persist(event);
        return event.getId();
    }
}
