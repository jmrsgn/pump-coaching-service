package com.johnmartin.coaching.mapper;

import com.johnmartin.coaching.dto.response.TrainingBlockResponse;
import com.johnmartin.coaching.entity.TrainingBlockEntity;

public class TrainingBlockMapper {

    private TrainingBlockMapper() {
    }

    public static TrainingBlockResponse toResponse(TrainingBlockEntity trainingBlock) {
        return new TrainingBlockResponse(trainingBlock.getId(),
                                         trainingBlock.getClientId(),
                                         trainingBlock.getTrainingBlockName(),
                                         trainingBlock.getNumberOfWeeks(),
                                         trainingBlock.getTrainingDays(),
                                         trainingBlock.getTrainingSplit(),
                                         trainingBlock.getEstimatedMacros(),
                                         trainingBlock.getTargetProteinInGrams(),
                                         trainingBlock.getTargetCarbsInGrams(),
                                         trainingBlock.getTargetFatInGrams(),
                                         trainingBlock.getRequiredDailySteps(),
                                         trainingBlock.getOtherNotes(),
                                         trainingBlock.getStatus(),
                                         trainingBlock.getCreatedAt(),
                                         trainingBlock.getUpdatedAt());
    }
}
