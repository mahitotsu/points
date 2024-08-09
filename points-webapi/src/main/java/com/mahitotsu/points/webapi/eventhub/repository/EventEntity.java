package com.mahitotsu.points.webapi.eventhub.repository;

import java.util.Random;
import java.util.UUID;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "Event")
@Table(indexes = {
        @Index(columnList = "eventTime,targetEntityName,eventType,targetEntityId")
})
@Getter
@ToString
@EqualsAndHashCode
@NamedQueries({
        @NamedQuery(name = EventEntity.FETCH_FROM_HEAD.NAME, query = EventEntity.FETCH_FROM_HEAD.___Q),
        @NamedQuery(name = EventEntity.FETCH_FROM_TAIL.NAME, query = EventEntity.FETCH_FROM_TAIL.___Q),
})
public class EventEntity {

    private static final Random SEED = new Random();

    public static interface FETCH_FROM_HEAD {
        String NAME = "Event.fetchFromHead";
        String ___Q = "select e from Event e"
                + " where eventTime >= :startTime"
                + " and eventTime < :stopTime"
                + " and targetEntityName = :targetEntityName"
                + " and eventType = :eventType"
                + " and targetEntityId = :targetEntityId"
                + " order by id asc";
    }

    public static interface FETCH_FROM_TAIL {
        String NAME = "Event.fetchFromTail";
        String ___Q = "select e from Event e"
                + " where eventTime <= :startTime"
                + " and eventTime > :stopTime"
                + " and targetEntityName = :targetEntityName"
                + " and eventType = :eventType"
                + " and targetEntityId = :targetEntityId"
                + " order by id desc";
    }

    EventEntity() {
    }

    public EventEntity(final String targetEntityName, final String targetEntityId, final String eventType,
            final Object payload) {
        this.targetEntityName = targetEntityName;
        this.targetEntityId = targetEntityId;
        this.eventType = eventType;
        this.payload = payload;
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

    @Type(JsonBinaryType.class)
    @Column(nullable = false, updatable = false, columnDefinition = "jsonb")
    private Object payload;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Long eventTime;

    @PrePersist
    protected void setup() {
        final long now = System.currentTimeMillis();
        this.id = new UUID(now, SEED.nextLong());
        this.eventTime = now;
    }
}
