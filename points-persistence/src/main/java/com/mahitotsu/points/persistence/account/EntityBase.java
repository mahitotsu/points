package com.mahitotsu.points.persistence.account;

import java.util.UUID;

import org.springframework.core.annotation.AnnotationUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
public class EntityBase {

    @SuppressWarnings("null")
    protected EntityBase() {
        try {
            this.entityName = AnnotationUtils.findAnnotation(this.getClass(), Entity.class).name();
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "Failed to detect the entity name and payload-type from annotation and type parameter.", e);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENTITY_ID")
    private UUID entityId;

    @Transient
    private String entityName;
}
