package com.example.gamestore.entity.enums;

public enum Status {
    ADOPTED("Adopted"),
    UNDER_CONSIDERATION("Under consideration"),
    PREPARE("Preparing"),
    READY("Ready"),
    CANCELED("Canceled");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Status: " + description;
    }
}
