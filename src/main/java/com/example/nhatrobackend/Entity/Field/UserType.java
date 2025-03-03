package com.example.nhatrobackend.Entity.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("TENANT")
    TENANT,
    @JsonProperty("LANDLORD")
    LANDLORD,
    @JsonProperty("MODERATOR")
    MODERATOR,
    @JsonProperty("ADMIN")
    ADMIN;
}
