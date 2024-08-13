package com.mahitotsu.points.webapi.account.repository;

import com.mahitotsu.points.webapi.jpa.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "account")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends EntityBase {

    public static enum Status {
        OPENED, CLOSED,
    }

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(nullable = false)
    private Integer points;
}
