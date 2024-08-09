package com.mahitotsu.points.webapi.eventhub.repository.impl;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.webapi.eventhub.repository.EventEntity;
import com.mahitotsu.points.webapi.eventhub.repository.EventRepository;
import com.mahitotsu.points.webapi.utils.EntityManagerUtils;
import com.mahitotsu.points.webapi.utils.MapUtils;

import jakarta.persistence.EntityManager;

@Repository
public class EventRepositoryImpl implements EventRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEventHistory(final String targetEntityName, final String targetEntityId,
            final String eventType, final long startTime, final long stopTime, final int maxResult) {

        return EntityManagerUtils.getEntityStream(
                this.entityManager, EventEntity.class,
                stopTime - startTime < 0 ? EventEntity.FETCH_FROM_TAIL.NAME : EventEntity.FETCH_FROM_HEAD.NAME,
                MapUtils.builder(new HashMap<String, Object>())
                        .put("targetEntityName", targetEntityName)
                        .put("targetEntityId", targetEntityId)
                        .put("eventType", eventType)
                        .put("startTime", startTime)
                        .put("stopTime", stopTime)
                        .build(),
                maxResult);
    }

    @Override
    @Transactional
    public UUID putEvent(final String targetEntityName, final String targetEntityId, final String eventType,
            final Object paylaod) {

        final EventEntity event = new EventEntity(targetEntityName, targetEntityId, eventType, paylaod);
        this.entityManager.persist(event);
        return event.getId();
    }
}
