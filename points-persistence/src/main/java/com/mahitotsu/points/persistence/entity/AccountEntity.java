package com.mahitotsu.points.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "Account")
@Table(indexes = {
        @Index(columnList = "number")
})
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends EntityBase {

    public AccountEntity(final String number) {
        this.number = number;
        this.available = true;
    }

    protected AccountEntity() {
        // for jpa
    }

    @Id
    @Column(nullable = false, updatable = false, length = 10)
    private String number;

    private boolean available;

    public void close() {
        this.available = false;
    }
}
