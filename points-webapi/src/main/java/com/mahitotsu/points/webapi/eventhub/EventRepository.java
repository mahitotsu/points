package com.mahitotsu.points.webapi.eventhub;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.webapi.domainobj.DomainObject;
import com.mahitotsu.points.webapi.eventhub.EventEntity.Payload;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

@Repository
public class EventRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Optional<EventEntity> fetchLastEvent(final Class<? extends DomainObject> targetObjectType,
            final UUID targetObjectId, final Class<? extends Payload> payloadType, final long eventTime) {
        return this.fetchEvents(targetObjectType, targetObjectId, payloadType, eventTime, -1, 1)
                .findFirst();
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEvents(final Class<? extends DomainObject> targetObjectType,
            final UUID targetObjectId, final long startTime, final long stopTime, final int maxResult) {
        return this.fetchEvents(targetObjectType, targetObjectId, Collections.emptySet(), startTime, stopTime,
                maxResult);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEvents(final Class<? extends DomainObject> targetObjectType,
            final UUID targetObjectId,
            final Class<? extends Payload> payloadType, final long startTime, final long stopTime,
            final int maxResult) {
        return this.fetchEvents(targetObjectType, targetObjectId, Collections.singleton(payloadType), startTime,
                stopTime, maxResult);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Stream<EventEntity> fetchEvents(final Class<? extends DomainObject> targetObjectType,
            final UUID targetObjectId,
            final Set<Class<? extends Payload>> payloadTypes, final long startTime, final long stopTime,
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

        if (targetObjectType != null) {
            sqlStatement.append(" and e.targetObjectType = :targetObjectType");
            params.put("targetObjectType", targetObjectType);
        }

        if (targetObjectId != null) {
            sqlStatement.append(" and e.targetObjectId = :targetObjectId");
            params.put("targetObjectId", targetObjectId);
        }

        if (payloadTypes != null && payloadTypes.isEmpty() == false) {
            sqlStatement.append(" and e.payloadType in :payloadTypes");
            params.put("payloadTypes", payloadTypes);
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
    public <T extends Payload> UUID putEvent(final Class<? extends DomainObject> targetObjectType,
            final UUID targetObjectId, final T event) {

        final EventEntity entity = new EventEntity(targetObjectType, targetObjectId, event);
        this.entityManager.persist(entity);
        return entity.getId();
    }
}
