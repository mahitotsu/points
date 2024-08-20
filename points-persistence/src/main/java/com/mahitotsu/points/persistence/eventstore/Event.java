package com.mahitotsu.points.persistence.eventstore;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.core.GenericTypeResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_name", discriminatorType = DiscriminatorType.STRING)
@Table(name = "event")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
public class Event<T> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Embeddable
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.NONE)
    @ToString
    @EqualsAndHashCode
    public static class EventId {
        @Column(name = "event_time", insertable = false, updatable = false, nullable = false)
        private ZonedDateTime time;
        @Column(name = "event_txid", insertable = false, updatable = false, nullable = false)
        private Long txid;
        @Column(name = "event_wxyz", insertable = false, updatable = false, nullable = false)
        private Double wxyz;
    }

    protected Event() {
        this.payloadType = GenericTypeResolver.resolveTypeArgument(this.getClass(), Event.class);
    }

    protected Event(final T payload, final UUID targetId, final String targetName) {
        this();

        this.payload = payload;
        this.targetId = targetId;
        this.targetName = targetName;
    }

    @Transient
    private Class<?> payloadType;

    @Transient
    private Object payload;

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private EventId id;

    @Column(name = "event_name", insertable = false, updatable = false, nullable = false)
    private String name;

    @Column(name = "event_payload", updatable = false, nullable = false)
    @Getter(AccessLevel.NONE)
    private String json;

    @Column(name = "target_name", updatable = false, nullable = false)
    private String targetName;

    @Column(name = "target_id", updatable = false, nullable = false)
    private UUID targetId;

    @PostPersist
    protected void objToJson() throws JsonProcessingException {
        if (this.payload != null) {
            this.json = mapper.writeValueAsString(this.payload);
        }
    }

    @PostLoad
    protected void jsonToObj() throws JsonMappingException, JsonProcessingException {
        if (this.json != null) {
            this.payload = mapper.readValue(this.json, this.payloadType);
        }
    }
}
