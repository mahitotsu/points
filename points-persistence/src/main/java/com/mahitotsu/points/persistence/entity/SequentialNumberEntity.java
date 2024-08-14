package com.mahitotsu.points.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "SequentialNumber")
@Table(indexes = {
        @Index(columnList = "name")
})
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SequentialNumberEntity extends EntityBase {

    public SequentialNumberEntity(final String name, final long initialValue) {
        this.name = name;
        this.value = initialValue;
    }

    @Column(nullable = false, unique = true, updatable = false)
    private String name;

    @Column(nullable = false)
    private Long value;

    public long increaseAndGet() {
        return ++this.value;
    }
}
