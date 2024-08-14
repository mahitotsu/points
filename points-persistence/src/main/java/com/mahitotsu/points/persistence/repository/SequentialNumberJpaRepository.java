package com.mahitotsu.points.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.mahitotsu.points.persistence.entity.SequentialNumberEntity;

import jakarta.persistence.LockModeType;

public interface SequentialNumberJpaRepository extends JpaRepository<SequentialNumberEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    SequentialNumberEntity getByName(String name);
}
