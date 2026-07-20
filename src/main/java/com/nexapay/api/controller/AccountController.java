package com.nexapay.api.controller;

import com.nexapay.api.dto.AmountRequest;
import com.nexapay.api.dto.OpenAccountRequest;
import com.nexapay.api.model.Account;
import com.nexapay.api.model.Transaction;
import com.nexapay.api.service.BankService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Bank account operations")
public class AccountController {

    private final BankService bankService;

    public AccountController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Open an account")
    public Account open(@Valid @RequestBody OpenAccountRequest request) {
        return bankService.openAccount(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by id")
    public Account getById(@PathVariable Long id) {
        return bankService.getAccount(id);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List accounts for a customer")
    public List<Account> listByCustomer(@PathVariable Long customerId) {
        return bankService.listAccountsByCustomer(customerId);
    }

    @PostMapping("/{id}/deposit")
    @Operation(summary = "Deposit funds into an account")
    public Account deposit(@PathVariable Long id, @Valid @RequestBody AmountRequest request) {
        return bankService.deposit(id, request);
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Withdraw funds from an account")
    public Account withdraw(@PathVariable Long id, @Valid @RequestBody AmountRequest request) {
        return bankService.withdraw(id, request);
    }

    @GetMapping("/{id}/transactions")
    @Operation(summary = "List transactions for an account")
    public List<Transaction> listTransactions(@PathVariable Long id) {
        return bankService.listTransactions(id);
    }
}
