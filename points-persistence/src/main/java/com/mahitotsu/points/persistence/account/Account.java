package com.mahitotsu.points.persistence.account;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "account")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
public class Account {

    protected Account() {
    }

    public Account(final String branchCode, final String accountNumber) {
        this.branchCode = branchCode;
        this.accountNumber = accountNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", insertable = false, updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "branch_code", updatable = false, nullable = false)
    private String branchCode;

    @Column(name = "account_number", updatable = false, nullable = false)
    private String accountNumber;
}
