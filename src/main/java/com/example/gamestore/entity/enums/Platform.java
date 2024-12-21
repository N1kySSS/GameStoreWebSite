package com.example.gamestore.entity.enums;

public enum Platform {
    PC("PC"),
    Xbox("Xbox"),
    Playstation("Playstation"),
    Nintendo("Nintendo switch");

    private final String description;

    Platform(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
