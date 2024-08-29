package com.mahitotsu.points.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString()
@EqualsAndHashCode()
@Embeddable
public class AccountIdentity {

    @Column(updatable = false)
    private String branchCode;
    
    @Column(updatable = false)
    private String accountNumber;
}
