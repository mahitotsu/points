package com.mahitotsu.points.persistence.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.persistence.entity.SequentialNumberEntity;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.LockModeType;

@Repository
public class SequentialNumberRepository extends AbstractRepository<SequentialNumberEntity, String> {

    public SequentialNumberRepository() {
        super(SequentialNumberEntity.class);
    }

    @Transactional
    public void registerSequentialNumberIfNotExists(final String name, final long initialValue) {
        if (name == null) {
            throw new IllegalArgumentException("The sequential number name is required.");
        }

        try {
            final SequentialNumberEntity sequentialNumber = new SequentialNumberEntity(name, initialValue);
            this.persist(sequentialNumber);
        } catch (EntityExistsException e) {
            return;
        }
    }

    @Transactional
    public long increaseAndGet(final String name) {

        final SequentialNumberEntity sequentialNumber = this
                .getById(name, LockModeType.PESSIMISTIC_WRITE)
                .orElse(null);
        if (sequentialNumber == null) {
            throw new IllegalStateException("The specified sequential number is not avaialble.");
        }
        return sequentialNumber.increaseAndGet();
    }
}
