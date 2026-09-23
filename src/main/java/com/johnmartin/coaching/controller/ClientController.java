package com.johnmartin.coaching.controller;

import com.johnmartin.coaching.constants.api.ApiConstants;
import com.johnmartin.coaching.dto.request.CreateClientUserRequest;
import com.johnmartin.coaching.dto.response.ClientUserResponse;
import com.johnmartin.coaching.dto.response.common.PagedResponse;
import com.johnmartin.coaching.dto.response.common.Result;
import com.johnmartin.coaching.service.ClientService;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.Path.API_CLIENTS)
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<Result<PagedResponse<ClientUserResponse>>> getClients(@RequestParam(defaultValue = "0") @PositiveOrZero int page) {
        PagedResponse<ClientUserResponse> users = clientService.getClients(page);
        return ResponseEntity.ok(Result.success(users));
    }

    @PostMapping(ApiConstants.Path.CREATE_CLIENT)
    public ResponseEntity<Result<Void>> createClient(@RequestBody CreateClientUserRequest request) {
        clientService.createClient(request);
        return ResponseEntity.ok(Result.success(null));
    }
}
