package com.mahitotsu.points.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString(callSuper = true)
public class EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private UUID id;
}
