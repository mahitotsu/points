package com.mahitotsu.points.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "Account")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends EntityBase {

    @Column(nullable = false, unique = true)
    private String number;
}
