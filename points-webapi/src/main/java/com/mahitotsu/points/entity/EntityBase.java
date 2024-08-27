package com.mahitotsu.points.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "entities")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "name", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
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

    @Column(name = "name", insertable = false, updatable = false, nullable = false)
    private String name;
}
