package com.example.gamestore.dto;

import com.example.gamestore.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class OrderDTO implements Serializable {
    private UUID id;

    private Status status;

    private int sum;

    private LocalDate dateOfOrder;

    private UUID userId;

    private UUID gameId;

    public OrderDTO(UUID id, Status status, int sum, LocalDate dateOfOrder, UUID userId, UUID gameId) {
        this.id = id;
        this.status = status;
        this.sum = sum;
        this.dateOfOrder = dateOfOrder;
        this.userId = userId;
        this.gameId = gameId;
    }

    protected OrderDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public LocalDate getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(LocalDate dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}
