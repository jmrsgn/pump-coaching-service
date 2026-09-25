package com.johnmartin.coaching.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.johnmartin.coaching.dto.response.TrainingBlockResponse;
import com.johnmartin.coaching.constants.error.ValidationErrorConstants;
import com.johnmartin.coaching.enums.TrainingBlockStatus;
import com.johnmartin.coaching.exceptions.GlobalExceptionHandler;
import com.johnmartin.coaching.service.TrainingBlockService;

class TrainingBlockControllerTest {

    private final UUID clientId = UUID.randomUUID();
    private final TrainingBlockService service = org.mockito.Mockito.mock(TrainingBlockService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new TrainingBlockController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void returnsCreatedBlockAndLocation() throws Exception {
        UUID blockId = UUID.randomUUID();
        when(service.createTrainingBlock(eq(clientId), any())).thenReturn(new TrainingBlockResponse(
                blockId, clientId, "Sample", 8, 5, "Upper/Lower", 2400, 160, 220, 65, 8000,
                null, TrainingBlockStatus.ACTIVE, Instant.parse("2026-09-23T00:00:00Z"), null));

        mvc.perform(post(createPath()).contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", path() + "/" + blockId))
                .andExpect(jsonPath("$.data.id").value(blockId.toString()))
                .andExpect(jsonPath("$.data.trainingBlockName").value("Sample"))
                .andExpect(jsonPath("$.data.trainingDays").value(5))
                .andExpect(jsonPath("$.data.estimatedMacros").value(2400))
                .andExpect(jsonPath("$.data.status").value("Active"));
    }

    @Test
    void rejectsMissingOrNegativeFieldsBeforeServiceCall() throws Exception {
        mvc.perform(post(createPath()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numberOfWeeks\":0,\"trainingSplit\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).createTrainingBlock(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"estimatedMacros", "targetProteinInGrams", "targetCarbsInGrams", "targetFatInGrams"})
    void acceptsZeroMacroValues(String field) throws Exception {
        when(service.createTrainingBlock(eq(clientId), any())).thenReturn(new TrainingBlockResponse(
                UUID.randomUUID(), clientId, "Sample", 8, 5, "Upper/Lower", 0, 160, 220, 65, 8000,
                null, TrainingBlockStatus.ACTIVE, Instant.parse("2026-09-23T00:00:00Z"), null));

        mvc.perform(post(createPath()).contentType(MediaType.APPLICATION_JSON)
                        .content(validBody().replace("\"" + field + "\":" + validValue(field),
                                                     "\"" + field + "\":0")))
                .andExpect(status().isCreated());

        verify(service).createTrainingBlock(eq(clientId), any());
    }

    @Test
    void returnsActiveBlock() throws Exception {
        UUID blockId = UUID.randomUUID();
        when(service.getActiveTrainingBlock(clientId)).thenReturn(new TrainingBlockResponse(
                blockId, clientId, "Sample", 8, 5, "Upper/Lower", 2400, 160, 220, 65, 8000,
                null, TrainingBlockStatus.ACTIVE, Instant.parse("2026-09-23T00:00:00Z"), null));

        mvc.perform(get(path() + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(blockId.toString()))
                .andExpect(jsonPath("$.data.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.data.status").value("Active"));

        verify(service).getActiveTrainingBlock(clientId);
    }

    @ParameterizedTest
    @MethodSource("invalidFields")
    void rejectsInvalidFieldsWithExplicitMessage(String field, String value, String message) throws Exception {
        String body = validBody().replace("\"" + field + "\":" + validValue(field),
                                          "\"" + field + "\":" + value);

        mvc.perform(post(createPath()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(message));

        verify(service, never()).createTrainingBlock(any(), any());
    }

    private static java.util.stream.Stream<Arguments> invalidFields() {
        return java.util.stream.Stream.of(
                Arguments.of("trainingBlockName", "null", ValidationErrorConstants.TRAINING_BLOCK_NAME_IS_REQUIRED),
                Arguments.of("trainingBlockName", "\" \"", ValidationErrorConstants.TRAINING_BLOCK_NAME_IS_REQUIRED),
                Arguments.of("numberOfWeeks", "null", ValidationErrorConstants.NUMBER_OF_WEEKS_CANNOT_BE_NULL),
                Arguments.of("trainingDays", "null", ValidationErrorConstants.TRAINING_DAYS_CANNOT_BE_NULL),
                Arguments.of("trainingSplit", "null", ValidationErrorConstants.TRAINING_SPLIT_CANNOT_BE_BLANK),
                Arguments.of("trainingSplit", "\" \"", ValidationErrorConstants.TRAINING_SPLIT_CANNOT_BE_BLANK),
                Arguments.of("estimatedMacros", "null", ValidationErrorConstants.ESTIMATED_MACROS_CANNOT_BE_NULL),
                Arguments.of("targetProteinInGrams", "null", ValidationErrorConstants.TARGET_PROTEIN_CANNOT_BE_NULL),
                Arguments.of("targetCarbsInGrams", "null", ValidationErrorConstants.TARGET_CARBS_CANNOT_BE_NULL),
                Arguments.of("targetFatInGrams", "null", ValidationErrorConstants.TARGET_FAT_CANNOT_BE_NULL),
                Arguments.of("requiredDailySteps", "null", ValidationErrorConstants.REQUIRED_DAILY_STEPS_CANNOT_BE_NULL),
                Arguments.of("numberOfWeeks", "0", ValidationErrorConstants.NUMBER_OF_WEEKS_MUST_BE_AT_LEAST_1),
                Arguments.of("trainingDays", "0", ValidationErrorConstants.TRAINING_DAYS_MUST_BE_AT_LEAST_1),
                Arguments.of("estimatedMacros", "-1", ValidationErrorConstants.ESTIMATED_MACROS_MUST_BE_ZERO_OR_GREATER),
                Arguments.of("targetProteinInGrams", "-1", ValidationErrorConstants.TARGET_PROTEIN_MUST_BE_ZERO_OR_GREATER),
                Arguments.of("targetCarbsInGrams", "-1", ValidationErrorConstants.TARGET_CARBS_MUST_BE_ZERO_OR_GREATER),
                Arguments.of("targetFatInGrams", "-1", ValidationErrorConstants.TARGET_FAT_MUST_BE_ZERO_OR_GREATER),
                Arguments.of("requiredDailySteps", "0", ValidationErrorConstants.REQUIRED_DAILY_STEPS_MUST_BE_GREATER_THAN_ZERO),
                Arguments.of("trainingSplit", "\"" + "x".repeat(101) + "\"", ValidationErrorConstants.TRAINING_SPLIT_MUST_BE_AT_MOST_100_CHARACTERS),
                Arguments.of("otherNotes", "\"" + "x".repeat(2001) + "\"", ValidationErrorConstants.OTHER_NOTES_MUST_BE_AT_MOST_2000_CHARACTERS));
    }

    private static String validValue(String field) {
        return switch (field) {
            case "trainingBlockName" -> "\"Sample\"";
            case "numberOfWeeks" -> "8";
            case "trainingDays" -> "5";
            case "trainingSplit" -> "\"Upper/Lower\"";
            case "estimatedMacros" -> "2400";
            case "targetProteinInGrams" -> "160";
            case "targetCarbsInGrams" -> "220";
            case "targetFatInGrams" -> "65";
            case "requiredDailySteps" -> "8000";
            case "otherNotes" -> "\"Travel notes\"";
            default -> throw new IllegalArgumentException(field);
        };
    }

    @Test
    void rejectsMalformedClientId() throws Exception {
        mvc.perform(post("/api/v1/clients/not-a-uuid/training-blocks/create")
                        .contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isBadRequest());

        verify(service, never()).createTrainingBlock(any(), any());
    }

    private String path() {
        return "/api/v1/clients/" + clientId + "/training-blocks";
    }

    private String createPath() {
        return path() + "/create";
    }

    private String validBody() {
        return """
                {"trainingBlockName":"Sample","numberOfWeeks":8,"trainingDays":5,
                 "trainingSplit":"Upper/Lower","estimatedMacros":2400,
                 "targetProteinInGrams":160,"targetCarbsInGrams":220,"targetFatInGrams":65,
                 "requiredDailySteps":8000,"otherNotes":"Travel notes"}
                """;
    }
}
