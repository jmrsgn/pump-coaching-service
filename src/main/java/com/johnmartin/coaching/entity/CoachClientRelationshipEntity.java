package com.johnmartin.coaching.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.johnmartin.coaching.constants.entities.CoachProfileConstants;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = CoachProfileConstants.CoachClientRelationship.TABLE_NAME)
@Getter
@Setter
public class CoachClientRelationshipEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @JoinColumn(nullable = false, name = CoachProfileConstants.CoachClientRelationship.COLUMN_COACH_ID)
    private UUID coachId;

    @JoinColumn(nullable = false, name = CoachProfileConstants.CoachClientRelationship.COLUMN_CLIENT_ID)
    private UUID clientId;

    @Column(nullable = false, name = CoachProfileConstants.CoachClientRelationship.COLUMN_STATUS)
    private String status;

    @Column(nullable = false, name = CoachProfileConstants.COLUMN_CREATED_AT)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = CoachProfileConstants.COLUMN_UPDATED_AT)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
