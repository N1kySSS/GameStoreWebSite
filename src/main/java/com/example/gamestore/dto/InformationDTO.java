package com.example.gamestore.dto;

import java.util.UUID;

public class InformationDTO {
    private UUID id;

    private boolean isChecked;

    private GameDTO game;

    private UserDTO user;

    public InformationDTO(UUID id, boolean isChecked, GameDTO game, UserDTO user) {
        this.id = id;
        this.isChecked = isChecked;
        this.game = game;
        this.user = user;
    }

    protected InformationDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
