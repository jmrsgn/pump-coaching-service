package com.johnmartin.coaching.entity;

import java.time.Instant;
import java.util.UUID;

import com.johnmartin.coaching.constants.entities.CoachClientRelationshipConstants;
import org.hibernate.annotations.CreationTimestamp;

import com.johnmartin.coaching.constants.entities.ClientProfileConstants;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = CoachClientRelationshipConstants.TABLE_NAME)
@Getter
@Setter
public class CoachClientRelationshipEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @JoinColumn(nullable = false, name = CoachClientRelationshipConstants.COLUMN_COACH_ID)
    private UUID coachId;

    @JoinColumn(nullable = false, name = CoachClientRelationshipConstants.COLUMN_CLIENT_ID)
    private UUID clientId;

    @Column(nullable = false, name = CoachClientRelationshipConstants.COLUMN_STATUS)
    private String status;

    @CreationTimestamp
    @Column(name = ClientProfileConstants.COLUMN_CREATED_AT, updatable = false)
    private Instant createdAt;

    @CreationTimestamp
    @Column(name = ClientProfileConstants.COLUMN_UPDATED_AT)
    private Instant updatedAt;
}
