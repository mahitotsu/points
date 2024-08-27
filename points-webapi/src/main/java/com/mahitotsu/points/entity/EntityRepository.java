package com.mahitotsu.points.entity;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Repository
public class EntityRepository {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean lockEntity(final UUID entityId, final LockModeType lockMode) {
        return this.entityManager.find(EntityBase.class, entityId, lockMode) != null;
    }
}
