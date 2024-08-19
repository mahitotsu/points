package com.mahitotsu.points.persistence.eventstore;

import java.util.UUID;

import org.hibernate.annotations.Type;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "Event")
@Table(name = "EVENT_STORE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EVENT_TYPE", discriminatorType = DiscriminatorType.STRING)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "class")
public abstract class EventEntity<T> {

    @SuppressWarnings({ "unchecked", "null" })
    public EventEntity() {
        try {
            this.eventType = AnnotationUtils.findAnnotation(this.getClass(), Entity.class).name();
            this.payloadType = (Class<T>) ResolvableType.forClass(this.getClass()).getSuperType().resolveGeneric(0);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "Failed to detect the entity name and payload-type from annotation and type parameter.", e);
        }
    }

    public void init(final T eventPayload, final String targetType, final UUID targetId) {
        if (this.eventId != null) {
            throw new IllegalStateException(
                    "This entity is presumed to be already persisted. You cannot modify this entity.");
        }

        this.eventPayload = eventPayload;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID", insertable = false, updatable = false, unique = true)
    private UUID eventId;

    @Column(name = "EVENT_TYPE", insertable = false, updatable = false)
    private String eventType;

    @Transient
    private Class<T> payloadType;

    @Type(JsonType.class)
    @Column(name = "EVENT_PAYLOAD", updatable = false)
    @NotNull
    @Valid
    private T eventPayload;

    @Column(name = "TARGET_TYPE", updatable = false)
    @NotBlank
    private String targetType;

    @Column(name = "TARGET_ID", updatable = false)
    @NotNull
    private UUID targetId;
}