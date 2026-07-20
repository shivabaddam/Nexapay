package com.nexapay.api.controller;

import com.nexapay.api.dto.TransferRequest;
import com.nexapay.api.service.BankService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "Move funds between accounts")
public class TransferController {

    private final BankService bankService;

    public TransferController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transfer funds between accounts")
    public Map<String, String> create(@Valid @RequestBody TransferRequest request) {
        bankService.transfer(request);
        return Map.of("status", "SUCCESS");
    }
}
