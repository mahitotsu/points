package com.mahitotsu.points.persistence.eventstore;

import java.util.UUID;

import org.springframework.core.annotation.AnnotationUtils;

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
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public abstract class EventEntity<T> {

    @SuppressWarnings({ "null" })
    public EventEntity() {
        try {
            this.eventType = AnnotationUtils.findAnnotation(this.getClass(), Entity.class).name();
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "Failed to detect the entity name and payload-type from annotation and type parameter.", e);
        }
    }

    public void init(final T eventPayload, final String targetType, final UUID targetId) {
        this.setEventPayload(eventPayload);
        this.setTargetType(targetType);
        this.setTargetId(targetId);
    }

    protected abstract void setEventPayload(final T eventPayload);
    public abstract T getEventPayload();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID", insertable = false, updatable = false, unique = true)
    private UUID eventId;

    @Column(name = "EVENT_TYPE", insertable = false, updatable = false)
    private String eventType;

    @Column(name = "TARGET_TYPE", updatable = false)
    @NotBlank
    private String targetType;

    @Column(name = "TARGET_ID", updatable = false)
    @NotNull
    private UUID targetId;
}