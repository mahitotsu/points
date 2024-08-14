package com.mahitotsu.points.webapi.jpa;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@MappedSuperclass
@Getter
@ToString
@EqualsAndHashCode
public class EntityBase implements Persistable<UUID> {

    private static Random SEED = new Random();

    @Id
    private UUID id;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastModifiedAt;

    @Override
    public boolean isNew() {
        return this.id != null && this.version != null;
    }

    @PrePersist
    protected final void prePersist() {
        final long now = System.currentTimeMillis();
        id = new UUID(now, SEED.nextLong());
        createdAt = Instant.ofEpochMilli(now);
        lastModifiedAt = createdAt;
    }

    @PreUpdate
    protected final void preUpdate() {
        final long now = System.currentTimeMillis();
        lastModifiedAt = Instant.ofEpochMilli(now);
    }
}
