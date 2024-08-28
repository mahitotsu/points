package com.mahitotsu.points.event;

import java.util.UUID;

import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahitotsu.points.entity.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventBase<T> extends EntityBase {

    private static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    protected EventBase() {
        super();
        this.payloadType = (Class<T>) ResolvableType.forClass(this.getClass()).getSuperType().getGeneric(0).resolve();
    }

    public EventBase(final UUID targetId, final T payload) {
        this();

        this.targetId = targetId;
        this.payload = payload;
    }

    private Class<T> payloadType;

    @Transient
    private T payload;

    @Column(name = "type", insertable = false, updatable = false, nullable = false)
    private String type;

    @Column(name = "target_id", updatable = false, nullable = false)
    private UUID targetId;

    @Getter(AccessLevel.NONE)
    @Column(name = "payload")
    private String payloadJson;

    @PrePersist
    private void serialize() throws JsonProcessingException {
        this.payloadJson = OBJECT_MAPPER.writeValueAsString(this.payload);
    }

    @PostLoad
    private void deserialize() throws JsonParseException, JsonProcessingException {
        this.payload = OBJECT_MAPPER.readValue(this.payloadJson, this.payloadType);
    }
}
