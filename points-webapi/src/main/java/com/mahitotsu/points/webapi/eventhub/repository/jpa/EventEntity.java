package com.mahitotsu.points.webapi.eventhub.repository.jpa;

import java.util.Random;
import java.util.UUID;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "Event")
@Table(indexes = {
        @Index(columnList = "eventTime, targetEntityName, targetEntityId,eventType")
})
@Getter
@ToString
@EqualsAndHashCode
public class EventEntity {

    private static final Random SEED = new Random();

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
