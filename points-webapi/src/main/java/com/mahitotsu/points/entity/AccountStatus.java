package com.mahitotsu.points.entity;

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
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountStatus extends EntityBase {

    public static enum Status {
        OPENED, CLOSED,
    }

    @Column(columnDefinition = "char(3)")
    private String branchCode;

    @Column(columnDefinition = "char(7)")
    private String accountNumber;

    @Enumerated(EnumType.ORDINAL)
    @Column
    private Status status;
}
