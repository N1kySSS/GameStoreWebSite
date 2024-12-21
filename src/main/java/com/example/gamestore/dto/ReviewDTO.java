package com.example.gamestore.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class ReviewDTO implements Serializable {
    private UUID id;

    private double rating;

    private String description;

    private LocalDate dateOfPublication;

    private UUID gameId;

    private UUID userId;

    public ReviewDTO(UUID id, double rating, String description, LocalDate dateOfPublication, UUID gameId, UUID userId) {
        this.id = id;
        this.rating = rating;
        this.description = description;
        this.dateOfPublication = dateOfPublication;
        this.gameId = gameId;
        this.userId = userId;
    }

    protected ReviewDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Min(value = 0)
    @Max(value = 5)
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(LocalDate dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
