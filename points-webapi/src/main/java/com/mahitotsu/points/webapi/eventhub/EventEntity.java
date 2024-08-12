package com.mahitotsu.points.webapi.eventhub;

import java.util.Random;
import java.util.UUID;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.mahitotsu.points.webapi.domainobj.DomainObject;

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
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Entity(name = "Event")
@Table(indexes = {
        @Index(columnList = "eventTime, targetObjectType, targetObjectId, payloadType")
})
@Getter
@ToString
@EqualsAndHashCode
public class EventEntity {

    @JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "class")
    @SuperBuilder
    @Jacksonized
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Payload {

    }

    private static final Random SEED = new Random();

    EventEntity() {
    }

    public <P extends Payload> EventEntity(final Class<? extends DomainObject> targetObjectType,
            final UUID targetObjectId, final P payload) {
        this.targetObjectType = targetObjectType;
        this.targetObjectId = targetObjectId;
        this.payloadType = payload.getClass();
        this.payload = payload;
    }

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Class<? extends DomainObject> targetObjectType;

    @Column(nullable = false, updatable = false)
    private UUID targetObjectId;

    @Column(nullable = false, updatable = false)
    private Class<? extends Payload> payloadType;

    @Type(JsonBinaryType.class)
    @Column(nullable = false, updatable = false, columnDefinition = "jsonb")
    private Payload payload;

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
