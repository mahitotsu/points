package com.mahitotsu.points.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Table(name = "entities")
@Value
@NoArgsConstructor(force = true)
public class EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

    @Column(name = "tx", insertable = false, updatable = false, nullable = false)
    private long tx;

    @Column(name = "sq", insertable = false, updatable = false, nullable = false)
    private int sq;

    @Column(name = "ts", insertable = false, updatable = false, nullable = false)
    private LocalDateTime ts;
}
