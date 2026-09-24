package com.johnmartin.coaching.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.johnmartin.coaching.enums.TrainingBlockStatus;

public record TrainingBlockResponse(UUID id,
                                    UUID clientId,
                                    String trainingBlockName,
                                    Integer numberOfWeeks,
                                    Integer trainingDays,
                                    String trainingSplit,
                                    Integer estimatedMacros,
                                    Integer targetProteinInGrams,
                                    Integer targetCarbsInGrams,
                                    Integer targetFatInGrams,
                                    Integer requiredDailySteps,
                                    String otherNotes,
                                    TrainingBlockStatus status,
                                    Instant createdAt,
                                    Instant updatedAt) {
}
