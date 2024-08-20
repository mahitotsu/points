package com.mahitotsu.points.event;

import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahitotsu.points.jpa.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "name", discriminatorType = DiscriminatorType.STRING)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Event<T> extends EntityBase {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    protected Event() {
        this.payloadType = (Class<T>) ResolvableType.forClass(this.getClass()).getSuperType().getGeneric(0).resolve();
    }

    public Event(final EntityBase target, final T payload) {
        this();
        this.target = target;
        this.payload = payload;
    }

    @Transient
    private Class<T> payloadType;

    @Transient
    private T payload;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "targetId", updatable = false, nullable = false)
    private EntityBase target;

    @Column(name = "payload", updatable = false)
    private String payloadJson;

    @PrePersist
    private void serialize() throws JsonProcessingException {
        this.payloadJson = mapper.writeValueAsString(this.payload);
    }

    @PostLoad
    private void deserialize() throws JsonMappingException, JsonProcessingException {
        this.payload = mapper.readValue(this.payloadJson, this.payloadType);
    }
}
