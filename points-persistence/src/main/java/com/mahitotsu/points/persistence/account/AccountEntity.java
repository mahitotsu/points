package com.mahitotsu.points.persistence.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "Account")
@Table(name = "ACCOUNT")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends EntityBase {

    AccountEntity() {
        super();
    }

    AccountEntity(final String branchCode, final String accountNumber) {
        this();
        this.branchCode = branchCode;
        this.accountNumber = accountNumber;
    }

    @Column(name = "BRANCH_CODE", updatable = false)
    @NotNull
    @Pattern(regexp = "[0-9]{3}")
    private String branchCode;

    @Column(name = "ACCOUNT_NUMBER", updatable = false)
    @NotNull
    @Pattern(regexp = "[0-9]{7}")
    private String accountNumber;
}
