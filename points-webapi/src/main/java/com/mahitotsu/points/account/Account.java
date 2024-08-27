package com.mahitotsu.points.account;

import com.mahitotsu.points.entity.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Account extends EntityBase {

    public static enum Status {
        OPENED, CLOSED,
    }

    @Column(name = "branch_code", updatable = false, nullable = false)
    private String branchCode;

    @Column(name = "account_number", updatable = false, nullable = false)
    private String accountNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Setter(AccessLevel.PUBLIC)
    private Status status;
}
