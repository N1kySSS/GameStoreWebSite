package com.example.gamestore.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class GameDTO implements Serializable {
    private UUID id;

    private int price;

    private double rating;

    private String name;

    private String picUri;

    private String developer;

    private String description;

    private LocalDate releaseData;

    private List<String> platforms;

    private List<GenreDTO> genres;

    private List<ReviewDTO> reviews;

    private Set<OrderDTO> orders;

    public GameDTO(UUID id, String picUri, int price, String name, String developer, String description, LocalDate releaseData, List<String> platforms, List<GenreDTO> genres) {
        this.id = id;
        this.picUri = picUri;
        this.price = price;
        this.rating = 0;
        this.name = name;
        this.developer = developer;
        this.description = description;
        this.releaseData = releaseData;
        this.platforms = platforms;
        this.genres = genres;
        this.reviews = null;
        this.orders = null;
    }

    protected GameDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Min(value = 0)
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Min(value = 0)
    @Max(value = 5)
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @NotNull
    @NotEmpty
    @Length(min = 3, message = "Game's name must be minimum three characters")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseData() {
        return releaseData;
    }

    public void setReleaseData(LocalDate releaseData) {
        this.releaseData = releaseData;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<GenreDTO> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreDTO> genres) {
        this.genres = genres;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    public Set<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderDTO> orders) {
        this.orders = orders;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }
}
