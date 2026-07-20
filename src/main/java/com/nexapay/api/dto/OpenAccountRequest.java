package com.nexapay.api.dto;

import com.nexapay.api.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OpenAccountRequest(
        @NotNull Long customerId,
        @NotNull AccountType type,
        @NotNull @DecimalMin(value = "0.00") BigDecimal initialDeposit,
        @Size(min = 3, max = 3) String currency
) {
}
