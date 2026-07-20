package com.nexapay.api.controller;

import com.nexapay.api.dto.CreateCustomerRequest;
import com.nexapay.api.model.Customer;
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
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Bank customer management")
public class CustomerController {

    private final BankService bankService;

    public CustomerController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public Customer create(@Valid @RequestBody CreateCustomerRequest request) {
        return bankService.createCustomer(request);
    }

    @GetMapping
    @Operation(summary = "List customers")
    public List<Customer> list() {
        return bankService.listCustomers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by id")
    public Customer getById(@PathVariable Long id) {
        return bankService.getCustomer(id);
    }
}
