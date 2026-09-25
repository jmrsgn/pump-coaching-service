package com.johnmartin.coaching.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.johnmartin.coaching.dto.AuthUser;
import com.johnmartin.coaching.constants.error.domain.TrainingBlockErrorConstants;
import com.johnmartin.coaching.dto.request.CreateTrainingBlockRequest;
import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;
import com.johnmartin.coaching.entity.TrainingBlockEntity;
import com.johnmartin.coaching.enums.TrainingBlockStatus;
import com.johnmartin.coaching.exceptions.ConflictException;
import com.johnmartin.coaching.exceptions.ForbiddenException;
import com.johnmartin.coaching.exceptions.NotFoundException;
import com.johnmartin.coaching.repository.ClientProfileRepository;
import com.johnmartin.coaching.repository.CoachClientRelationshipRepository;
import com.johnmartin.coaching.repository.TrainingBlockRepository;
import com.johnmartin.coaching.security.AuthContext;

@ExtendWith(MockitoExtension.class)
class TrainingBlockServiceTest {

    private static final UUID COACH_ID = UUID.randomUUID();
    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final CreateTrainingBlockRequest REQUEST = new CreateTrainingBlockRequest("Sample",
                                                                                             8,
                                                                                             5,
                                                                                             " Upper/Lower ",
                                                                                             2400,
                                                                                             160,
                                                                                             220,
                                                                                             65,
                                                                                             8000,
                                                                                             " Travel notes ");

    @Mock
    AuthService authService;
    @Mock
    CoachClientRelationshipRepository relationshipRepository;
    @Mock
    ClientProfileRepository clientProfileRepository;
    @Mock
    TrainingBlockRepository trainingBlockRepository;
    @InjectMocks
    TrainingBlockService service;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getAuthUser()).thenReturn(new AuthUser(COACH_ID.toString(), null, null, null, null));
        AuthContext.set(new AuthUser(COACH_ID.toString(), null, null, null, null), false);
    }

    @AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void createsActiveBlockForActiveRelationship() {
        relationship("active");
        when(clientProfileRepository.existsByUserId(CLIENT_ID)).thenReturn(true);
        when(trainingBlockRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            TrainingBlockEntity block = invocation.getArgument(0);
            block.setCreatedAt(Instant.parse("2026-09-23T00:00:00Z"));
            return block;
        });

        var response = service.createTrainingBlock(CLIENT_ID, REQUEST);

        ArgumentCaptor<TrainingBlockEntity> saved = ArgumentCaptor.forClass(TrainingBlockEntity.class);
        verify(trainingBlockRepository).saveAndFlush(saved.capture());
        assertEquals(COACH_ID, saved.getValue().getCoachId());
        assertEquals("Sample", saved.getValue().getTrainingBlockName());
        assertEquals(8, saved.getValue().getNumberOfWeeks());
        assertEquals(5, saved.getValue().getTrainingDays());
        assertEquals(2400, saved.getValue().getEstimatedMacros());
        assertEquals(160, saved.getValue().getTargetProteinInGrams());
        assertEquals(220, saved.getValue().getTargetCarbsInGrams());
        assertEquals(65, saved.getValue().getTargetFatInGrams());
        assertEquals(CLIENT_ID, response.clientId());
        assertEquals("Sample", response.trainingBlockName());
        assertEquals(8, response.numberOfWeeks());
        assertEquals(5, response.trainingDays());
        assertEquals(2400, response.estimatedMacros());
        assertEquals(160, response.targetProteinInGrams());
        assertEquals(220, response.targetCarbsInGrams());
        assertEquals(65, response.targetFatInGrams());
        assertEquals(TrainingBlockStatus.ACTIVE, response.status());
        assertEquals("Upper/Lower", response.trainingSplit());
        assertEquals("Travel notes", response.otherNotes());
        assertEquals(8000, response.requiredDailySteps());
        assertTrue(response.id() != null);
        verify(relationshipRepository).findByCoachIdAndClientIdForUpdate(COACH_ID, CLIENT_ID);
    }

    @Test
    void deniesCoachWithoutRelationship() {
        when(relationshipRepository.findByCoachIdAndClientIdForUpdate(COACH_ID,
                                                                      CLIENT_ID)).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> service.createTrainingBlock(CLIENT_ID, REQUEST));
        verify(trainingBlockRepository, never()).saveAndFlush(any());
    }

    @Test
    void deniesInactiveRelationship() {
        relationship("inactive");

        assertThrows(ForbiddenException.class, () -> service.createTrainingBlock(CLIENT_ID, REQUEST));
        verify(trainingBlockRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsSecondActiveBlock() {
        relationship("active");
        when(clientProfileRepository.existsByUserId(CLIENT_ID)).thenReturn(true);
        when(trainingBlockRepository.existsByCoachIdAndClientIdAndStatus(COACH_ID,
                                                                         CLIENT_ID,
                                                                         TrainingBlockStatus.ACTIVE)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createTrainingBlock(CLIENT_ID, REQUEST));
        verify(trainingBlockRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsMissingClientProfile() {
        relationship("active");

        assertThrows(NotFoundException.class, () -> service.createTrainingBlock(CLIENT_ID, REQUEST));
        verify(trainingBlockRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsInternalTokenEvenWithUserId() {
        AuthContext.set(new AuthUser(COACH_ID.toString(), null, null, null, null), true);

        assertThrows(ForbiddenException.class, () -> service.createTrainingBlock(CLIENT_ID, REQUEST));
        verify(relationshipRepository, never()).findByCoachIdAndClientIdForUpdate(any(), any());
    }

    @Test
    void returnsActiveBlockForEnrolledClient() {
        activeReadRelationship("active");
        when(clientProfileRepository.existsByUserId(CLIENT_ID)).thenReturn(true);
        TrainingBlockEntity block = new TrainingBlockEntity();
        UUID blockId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-09-23T00:00:00Z");
        block.setId(blockId);
        block.setCoachId(COACH_ID);
        block.setClientId(CLIENT_ID);
        block.setTrainingBlockName("Sample");
        block.setNumberOfWeeks(8);
        block.setTrainingDays(5);
        block.setTrainingSplit("Upper/Lower");
        block.setEstimatedMacros(2400);
        block.setTargetProteinInGrams(160);
        block.setTargetCarbsInGrams(220);
        block.setTargetFatInGrams(65);
        block.setRequiredDailySteps(8000);
        block.setOtherNotes("Travel notes");
        block.setStatus(TrainingBlockStatus.ACTIVE);
        block.setCreatedAt(createdAt);
        block.setUpdatedAt(createdAt);
        when(trainingBlockRepository.findByCoachIdAndClientIdAndStatus(COACH_ID, CLIENT_ID,
                                                                       TrainingBlockStatus.ACTIVE)).thenReturn(Optional.of(block));

        var response = service.getActiveTrainingBlock(CLIENT_ID);

        assertEquals(blockId, response.id());
        assertEquals(CLIENT_ID, response.clientId());
        assertEquals("Sample", response.trainingBlockName());
        assertEquals(8, response.numberOfWeeks());
        assertEquals(5, response.trainingDays());
        assertEquals("Upper/Lower", response.trainingSplit());
        assertEquals(2400, response.estimatedMacros());
        assertEquals(160, response.targetProteinInGrams());
        assertEquals(220, response.targetCarbsInGrams());
        assertEquals(65, response.targetFatInGrams());
        assertEquals(8000, response.requiredDailySteps());
        assertEquals("Travel notes", response.otherNotes());
        assertEquals(TrainingBlockStatus.ACTIVE, response.status());
        assertEquals(createdAt, response.createdAt());
        assertEquals(createdAt, response.updatedAt());
        verify(relationshipRepository).findByCoachIdAndClientId(COACH_ID, CLIENT_ID);
    }

    @Test
    void deniesActiveBlockReadWithoutRelationship() {
        var exception = assertThrows(ForbiddenException.class, () -> service.getActiveTrainingBlock(CLIENT_ID));

        assertEquals(TrainingBlockErrorConstants.CLIENT_IS_NOT_ENROLLED_TO_COACH, exception.getMessage());
        verify(trainingBlockRepository, never()).findByCoachIdAndClientIdAndStatus(any(), any(), any());
    }

    @Test
    void deniesActiveBlockReadForInactiveRelationship() {
        activeReadRelationship("inactive");

        var exception = assertThrows(ForbiddenException.class, () -> service.getActiveTrainingBlock(CLIENT_ID));

        assertEquals(TrainingBlockErrorConstants.CLIENT_IS_NOT_ENROLLED_TO_COACH, exception.getMessage());
        verify(trainingBlockRepository, never()).findByCoachIdAndClientIdAndStatus(any(), any(), any());
    }

    @Test
    void rejectsMissingProfileOnActiveBlockRead() {
        activeReadRelationship("active");

        var exception = assertThrows(NotFoundException.class, () -> service.getActiveTrainingBlock(CLIENT_ID));

        assertEquals(TrainingBlockErrorConstants.CLIENT_NOT_FOUND, exception.getMessage());
        verify(trainingBlockRepository, never()).findByCoachIdAndClientIdAndStatus(any(), any(), any());
    }

    @Test
    void reportsMissingActiveBlockForEnrolledClient() {
        activeReadRelationship("active");
        when(clientProfileRepository.existsByUserId(CLIENT_ID)).thenReturn(true);

        var exception = assertThrows(NotFoundException.class, () -> service.getActiveTrainingBlock(CLIENT_ID));

        assertEquals(TrainingBlockErrorConstants.NO_ACTIVE_TRAINING_BLOCK_EXISTS_FOR_THIS_CLIENT, exception.getMessage());
        verify(trainingBlockRepository).findByCoachIdAndClientIdAndStatus(COACH_ID, CLIENT_ID, TrainingBlockStatus.ACTIVE);
    }

    @Test
    void rejectsInternalRequestOnActiveBlockRead() {
        AuthContext.set(new AuthUser(COACH_ID.toString(), null, null, null, null), true);

        var exception = assertThrows(ForbiddenException.class, () -> service.getActiveTrainingBlock(CLIENT_ID));

        assertEquals(TrainingBlockErrorConstants.USER_AUTHENTICATION_REQUIRED, exception.getMessage());
        verify(relationshipRepository, never()).findByCoachIdAndClientId(any(), any());
    }

    private void activeReadRelationship(String status) {
        CoachClientRelationshipEntity relationship = new CoachClientRelationshipEntity();
        relationship.setStatus(status);
        when(relationshipRepository.findByCoachIdAndClientId(COACH_ID,
                                                               CLIENT_ID)).thenReturn(Optional.of(relationship));
    }

    private void relationship(String status) {
        CoachClientRelationshipEntity relationship = new CoachClientRelationshipEntity();
        relationship.setStatus(status);
        when(relationshipRepository.findByCoachIdAndClientIdForUpdate(COACH_ID,
                                                                      CLIENT_ID)).thenReturn(Optional.of(relationship));
    }
}
