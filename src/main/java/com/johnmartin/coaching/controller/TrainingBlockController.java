package com.johnmartin.coaching.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.request.CreateTrainingBlockRequest;
import com.johnmartin.coaching.dto.response.TrainingBlockResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.service.TrainingBlockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConstants.Path.API_TRAINING_BLOCKS)
public class TrainingBlockController {

    private final TrainingBlockService trainingBlockService;

    public TrainingBlockController(TrainingBlockService trainingBlockService) {
        this.trainingBlockService = trainingBlockService;
    }

    @PostMapping(ApiConstants.Path.CREATE_TRAINING_BLOCK)
    public ResponseEntity<Result<TrainingBlockResponse>> createTrainingBlock(@PathVariable UUID clientId,
                                                                             @Valid @RequestBody CreateTrainingBlockRequest request) {
        TrainingBlockResponse block = trainingBlockService.createTrainingBlock(clientId, request);
        return ResponseEntity.created(URI.create(ApiConstants.Path.API_TRAINING_BLOCKS.replace("{clientId}",
                                                                                               clientId.toString())
                                                 + "/" + block.id()))
                             .body(Result.success(block));
    }
}
