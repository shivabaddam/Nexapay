package com.nexapay.api.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private final Long id;
    private final Long accountId;
    private final Long relatedAccountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final Instant createdAt;

    public Transaction(Long id,
                       Long accountId,
                       Long relatedAccountId,
                       TransactionType type,
                       BigDecimal amount,
                       String description,
                       Instant createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.relatedAccountId = relatedAccountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getRelatedAccountId() {
        return relatedAccountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
