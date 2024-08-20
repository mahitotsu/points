package com.mahitotsu.points.jpa;

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
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "entity_base")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "name", discriminatorType = DiscriminatorType.STRING)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
public class EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private UUID id;

    @Column(name = "tm", nullable = false, insertable = false, updatable = false)
    private LocalDateTime tm;

    @Column(name = "tx", nullable = false, insertable = false, updatable = false)
    private Long tx;

    @Column(name = "name", nullable = false, insertable = false, updatable = false)
    private String name;
}
