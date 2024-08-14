package com.mahitotsu.points.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahitotsu.points.persistence.entity.AccountEntity;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    
}
