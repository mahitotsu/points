package com.mahitotsu.points.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@MappedSuperclass
@Getter
@ToString
@EqualsAndHashCode
public class EntityBase {

    @Version
    private Long version;
}
