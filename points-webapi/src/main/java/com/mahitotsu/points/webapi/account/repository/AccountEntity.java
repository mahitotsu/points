package com.mahitotsu.points.webapi.account.repository;

import com.mahitotsu.points.webapi.jpa.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "account")
@Table(name = "accounts", indexes = {
        @Index(columnList = "number", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = { "number" })
})
@NamedQueries({
        @NamedQuery(name = AccountEntity.GET_LAST_ACCOUNT_NUMBER, query = "select a.number from account a where a.number is not null order by a.number desc")
})
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends EntityBase {

    public static enum Status {
        OPENED, CLOSED,
    }

    public static final String GET_LAST_ACCOUNT_NUMBER = "Accoiunt.getLastAccountNumber";

    AccountEntity(final String number) {
        this.number = number;
    }

    protected AccountEntity() {
        // for JPA
    }

    @Column(nullable = false, unique = true, updatable = false)
    @Setter(AccessLevel.NONE)
    private String number;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(nullable = false)
    private Integer points;
}
