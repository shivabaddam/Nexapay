package com.nexapay.api.model;

import java.time.Instant;

public class Customer {
    private final Long id;
    private final String fullName;
    private final String email;
    private final Instant createdAt;

    public Customer(Long id, String fullName, String email, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
