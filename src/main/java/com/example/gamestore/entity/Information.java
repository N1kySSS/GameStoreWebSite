package com.example.gamestore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "information")
public class Information extends BaseEntity{
    private boolean isChecked;

    private Game game;

    private User user;

    public Information(Game game, User user, boolean isChecked) {
        this.game = game;
        this.user = user;
        this.isChecked = isChecked;
    }

    protected Information() {}

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "isChecked", nullable = false)
    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean checked) {
        this.isChecked = checked;
    }
}
