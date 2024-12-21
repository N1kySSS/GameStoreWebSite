package com.example.gamestore.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class GenreDTO {
    private UUID id;

    private String name;

    private String description;

    private List<String> games;

    public GenreDTO(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.games = null;
    }

    protected GenreDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGames() {
        return games;
    }

    public void setGames(List<String> games) {
        this.games = games;
    }
}
