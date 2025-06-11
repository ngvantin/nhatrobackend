package com.example.nhatrobackend.Entity.Field;

public enum FurnitureStatus {
    FULL("FULL"),
    EMPTY("EMPTY");

    private final String value;

    FurnitureStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FurnitureStatus fromValue(String value) {
        for (FurnitureStatus status : FurnitureStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown FurnitureStatus value: " + value);
    }
}
