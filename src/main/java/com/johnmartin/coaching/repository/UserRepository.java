package com.johnmartin.coaching.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.johnmartin.coaching.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
