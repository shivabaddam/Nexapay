package com.nexapay.api.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Account {
    private final Long id;
    private final Long customerId;
    private final AccountType type;
    private final String currency;
    private final Instant openedAt;
    private BigDecimal balance;

    public Account(Long id,
                   Long customerId,
                   AccountType type,
                   String currency,
                   BigDecimal balance,
                   Instant openedAt) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.openedAt = openedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public AccountType getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}
