package com.johnmartin.coaching.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.johnmartin.coaching.entity.ClientProfileEntity;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfileEntity, UUID> {

    boolean existsByUserId(UUID userId);
}
