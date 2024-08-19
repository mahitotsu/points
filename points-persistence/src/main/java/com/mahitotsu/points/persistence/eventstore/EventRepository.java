package com.mahitotsu.points.persistence.eventstore;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Repository
public class EventRepository {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Autowired
    private EntityManager entityMangaer;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String getEventType(final Class<? extends EventEntity<?>> entityType) {
        return this.entityMangaer.getMetamodel().entity(entityType).getName();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long extractEpochMillis(final EventEntity<?> event) {
        try {
            return DATE_FORMAT.parse(Stream.ofNullable(event.getEventId())
                    .flatMap(u -> Arrays.stream(u.toString().split("-")))
                    .limit(4).collect(Collectors.joining()).substring(0, 17)).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to extract epoc time from the specified event id.", e);
        }
    }

    @Transactional
    public long putEvent(final EventEntity<?> event) {
        this.entityMangaer.persist(event);
        return this.extractEpochMillis(event);
    }

    @Transactional(readOnly = true)
    public Optional<EventEntity<?>> findEventById(final UUID eventId) {
        return Optional.ofNullable(this.entityMangaer.find(EventEntity.class, eventId));
    }

    @Transactional(readOnly = true)
    public Optional<EventEntity<?>> findLastEvent(final String eventType, final String targetType,
            final UUID targetId) {
        @SuppressWarnings("unchecked")
        final List<EventEntity<?>> reusltList = this.entityMangaer.createQuery("""
                select e from Event e
                where e.eventType = :eventType
                    and e.targetType = :targetType
                    and e.targetId = :targetId order by e.id desc
                        """)
                .setParameter("eventType", eventType)
                .setParameter("targetType", targetType)
                .setParameter("targetId", targetId)
                .setMaxResults(1)
                .getResultList();
        return Optional.ofNullable(reusltList.isEmpty() ? null : reusltList.get(0));
    }

    @Transactional(readOnly = true)
    public Stream<EventEntity<?>> streamEvents(final String eventType, final String targetType, final UUID targetId,
            final long startTime,
            final long endTime) {
        final boolean asc = (endTime - startTime >= 0);
        return asc ? this.streamEvents(eventType, targetType, targetId, this.floorId(startTime), this.floorId(endTime))
                : this.streamEvents(eventType, targetType, targetId, this.ceilingId(startTime),
                        this.ceilingId(endTime));
    }

    @Transactional(readOnly = true)
    public Stream<EventEntity<?>> streamEvents(final String eventType, final String targetType, final UUID targetId,
            final UUID startId,
            final UUID endId) {
        final boolean asc = (endId.toString().compareTo(startId.toString()) >= 0);
        @SuppressWarnings("unchecked")
        final Stream<EventEntity<?>> eventStream = this.entityMangaer.createQuery("select e from Event e where " +
                (asc ? "e.id >= :startId and e.id < :endId" : " e.id <= :startId and e.id > :endId")
                + " and e.eventType = :eventType and e.targetType = :targetType and e.targetId = :targetId order by e.id"
                + (asc ? " asc" : " desc"))
                .setParameter("startId", startId)
                .setParameter("endId", endId)
                .setParameter("eventType", eventType)
                .setParameter("targetType", targetType)
                .setParameter("targetId", targetId)
                .getResultStream();
        return eventStream;
    }

    private UUID ceilingId(final long epochMillis) {
        final String ts = DATE_FORMAT.format(new Timestamp(epochMillis));
        return UUID.fromString(String.join("-", new String[] {
                ts.substring(0, 8),
                ts.substring(8, 12),
                ts.substring(12, 16),
                ts.substring(16, 17) + "999",
                "ffffffffffff" }));
    }

    private UUID floorId(final long epochMillis) {
        final String ts = DATE_FORMAT.format(new Timestamp(epochMillis));
        return UUID.fromString(String.join("-", new String[] {
                ts.substring(0, 8),
                ts.substring(8, 12),
                ts.substring(12, 16),
                ts.substring(16, 17) + "000",
                "000000000000" }));
    }
}
