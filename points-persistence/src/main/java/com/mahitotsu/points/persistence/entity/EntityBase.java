package com.mahitotsu.points.persistence.entity;

import java.util.Random;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@MappedSuperclass
@Getter
@ToString
@EqualsAndHashCode
public class EntityBase {

    private static final Random SEED = new Random();

    @Id
    @Column(updatable = false)
    private UUID id;

    @Version
    private Long version;

    @PrePersist
    protected void initialize() {
        final long now = System.currentTimeMillis();
        this.id = new UUID(now, SEED.nextLong());
    }
}
