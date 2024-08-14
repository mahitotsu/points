package com.mahitotsu.points.webapi.permission.repository;

import com.mahitotsu.points.webapi.jpa.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity(name = "Permission")
@Table(name = "permissions", indexes = {
        @Index(columnList = "serviceName")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = "serviceName")
})
@NamedQueries({
        @NamedQuery(name = PermissionEntity.GET_BY_SERVICE, query = "select p from Permission p where p.serviceName = :serviceName")
})
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PermissionEntity extends EntityBase {

    public static final String GET_BY_SERVICE = "Lock.getByService";

    public PermissionEntity(final String serviceName) {
        this.serviceName = serviceName;
    }

    protected PermissionEntity() {
        // for JPA
    }

    @Column(nullable = false, unique = true)
    @Setter(AccessLevel.NONE)
    private String serviceName;
}
