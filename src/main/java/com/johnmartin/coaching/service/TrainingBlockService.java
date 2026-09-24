package com.johnmartin.coaching.service;

import java.util.UUID;

import com.johnmartin.coaching.utilities.LoggerUtility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.johnmartin.coaching.constants.error.domain.TrainingBlockErrorConstants;
import com.johnmartin.coaching.dto.request.CreateTrainingBlockRequest;
import com.johnmartin.coaching.dto.response.TrainingBlockResponse;
import com.johnmartin.coaching.entity.CoachClientRelationshipEntity;
import com.johnmartin.coaching.entity.TrainingBlockEntity;
import com.johnmartin.coaching.enums.CoachingStatus;
import com.johnmartin.coaching.enums.TrainingBlockStatus;
import com.johnmartin.coaching.exceptions.ConflictException;
import com.johnmartin.coaching.exceptions.ForbiddenException;
import com.johnmartin.coaching.exceptions.NotFoundException;
import com.johnmartin.coaching.repository.ClientProfileRepository;
import com.johnmartin.coaching.repository.CoachClientRelationshipRepository;
import com.johnmartin.coaching.repository.TrainingBlockRepository;
import com.johnmartin.coaching.security.AuthContext;

@Service
public class TrainingBlockService {

    public static final Class<TrainingBlockService> clazz = TrainingBlockService.class;

    private final AuthService authService;
    private final CoachClientRelationshipRepository coachClientRelationshipRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final TrainingBlockRepository trainingBlockRepository;

    public TrainingBlockService(AuthService authService,
                                CoachClientRelationshipRepository relationshipRepository,
                                ClientProfileRepository clientProfileRepository,
                                TrainingBlockRepository trainingBlockRepository) {
        this.authService = authService;
        this.coachClientRelationshipRepository = relationshipRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.trainingBlockRepository = trainingBlockRepository;
    }

    @Transactional
    public TrainingBlockResponse createTrainingBlock(UUID clientId, CreateTrainingBlockRequest request) {
        // To ensure coach is creating the training block directly in this service, not another internal service
        if (AuthContext.isInternalRequest()) {
            throw new ForbiddenException(TrainingBlockErrorConstants.USER_AUTHENTICATION_REQUIRED);
        }

        String userId = authService.getAuthUser().id();
        UUID coachId = UUID.fromString(userId);

        // Check if client is enrolled to coach
        CoachClientRelationshipEntity relationship = coachClientRelationshipRepository.findByCoachIdAndClientIdForUpdate(coachId,
                                                                                                                         clientId)
                                                                                      .orElseThrow(() -> new ForbiddenException(TrainingBlockErrorConstants.CLIENT_IS_NOT_ENROLLED_TO_COACH));

        if (!CoachingStatus.ACTIVE.getValue().equalsIgnoreCase(relationship.getStatus())) {
            throw new ForbiddenException(TrainingBlockErrorConstants.CLIENT_IS_NOT_ENROLLED_TO_COACH);
        }

        if (!clientProfileRepository.existsByUserId(clientId)) {
            throw new NotFoundException(TrainingBlockErrorConstants.CLIENT_NOT_FOUND);
        }

        if (trainingBlockRepository.existsByCoachIdAndClientIdAndStatus(coachId,
                                                                        clientId,
                                                                        TrainingBlockStatus.ACTIVE)) {
            throw new ConflictException(TrainingBlockErrorConstants.ACTIVE_TRAINING_BLOCK_ALREADY_EXISTS_FOR_THIS_CLIENT);
        }

        // Create training block entity
        TrainingBlockEntity trainingBlock = new TrainingBlockEntity();
        trainingBlock.setId(UUID.randomUUID());
        trainingBlock.setCoachId(coachId);
        trainingBlock.setClientId(clientId);
        trainingBlock.setStatus(TrainingBlockStatus.ACTIVE);
        trainingBlock.setTrainingBlockName(request.trainingBlockName().trim());
        trainingBlock.setNumberOfWeeks(request.numberOfWeeks());
        trainingBlock.setTrainingDays(request.trainingDays());
        trainingBlock.setTrainingSplit(request.trainingSplit().trim());
        trainingBlock.setEstimatedMacros(request.estimatedMacros());
        trainingBlock.setTargetProteinInGrams(request.targetProteinInGrams());
        trainingBlock.setTargetCarbsInGrams(request.targetCarbsInGrams());
        trainingBlock.setTargetFatInGrams(request.targetFatInGrams());
        trainingBlock.setRequiredDailySteps(request.requiredDailySteps());
        trainingBlock.setOtherNotes(request.otherNotes() == null
                                    || request.otherNotes().isBlank() ? null : request.otherNotes().trim());

        TrainingBlockEntity saved = trainingBlockRepository.saveAndFlush(trainingBlock);
        LoggerUtility.d(clazz, "Training block has been saved successfully");
        return new TrainingBlockResponse(saved.getId(),
                                         saved.getClientId(),
                                         saved.getTrainingBlockName(),
                                         saved.getNumberOfWeeks(),
                                         saved.getTrainingDays(),
                                         saved.getTrainingSplit(),
                                         saved.getEstimatedMacros(),
                                         saved.getTargetProteinInGrams(),
                                         saved.getTargetCarbsInGrams(),
                                         saved.getTargetFatInGrams(),
                                         saved.getRequiredDailySteps(),
                                         saved.getOtherNotes(),
                                         saved.getStatus(),
                                         saved.getCreatedAt(),
                                         saved.getUpdatedAt());
    }
}
