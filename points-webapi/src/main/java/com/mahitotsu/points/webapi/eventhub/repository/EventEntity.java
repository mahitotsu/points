package com.mahitotsu.points.webapi.eventhub.repository;

import java.util.Random;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "Event")
@Getter
@ToString
@EqualsAndHashCode
@NamedQueries({
        @NamedQuery(name = EventEntity.FETCH_EVENT_HISTORY.NAME, query = EventEntity.FETCH_EVENT_HISTORY.___Q),
})
public class EventEntity {

    private static final Random SEED = new Random();

    public static interface FETCH_EVENT_HISTORY {
        String NAME = "Event.fetchEventHistory";
        String ___Q = "select e from Event e"
                + " where targetEntityName = :targetEntityName"
                + " and targetEntityId = :targetEntityId"
                + " and eventType = :eventType"
                + " and eventTime >= :startTime"
                + " and eventTime < :stopTime"
                + " order by id asc";
    }

    public EventEntity(final String targetEntityName, final String targetEntityId, final String eventType) {
        this.targetEntityName = targetEntityName;
        this.targetEntityId = targetEntityId;
        this.eventType = eventType;
    }

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private String targetEntityName;

    @Column(nullable = false, updatable = false)
    private String targetEntityId;

    @Column(nullable = false, updatable = false)
    private String eventType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Long eventTime;

    protected void setup() {
        final long now = System.currentTimeMillis();
        this.id = new UUID(now, SEED.nextLong());
        this.eventTime = now;
    }
}
