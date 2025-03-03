package com.example.nhatrobackend.Entity.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserStatus {
    @JsonProperty("ACTIVE")
    ACTIVE,
    @JsonProperty("LOCKED")
    LOCKED;
}
