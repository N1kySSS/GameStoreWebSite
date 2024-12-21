package com.example.gamestore.entity;

import com.example.gamestore.entity.enums.Platform;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "game")
public class Game extends BaseEntity{
    private String picUri;

    private int price;

    private double rating;

    private String name;

    private String developer;

    private String description;

    private LocalDate releaseData;

    private List<Platform> platforms;

    private List<Genre> genres;

    private List<Review> reviews;

    private List<Information> informationList;

    private Set<Order> orders;

    public Game(String name, int price, String description, String developer, LocalDate releaseData, List<Platform> platforms, String picUri) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.developer = developer;
        this.releaseData = releaseData;
        this.platforms = platforms;
        this.picUri = picUri;
    }

    protected Game() {}

    @Column(name = "picUri")
    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "price", nullable = false)
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "developer", nullable = false)
    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    @Column(name = "releaseDate", nullable = false)
    public LocalDate getReleaseData() {
        return releaseData;
    }

    public void setReleaseData(LocalDate releaseData) {
        this.releaseData = releaseData;
    }

    @Enumerated(EnumType.ORDINAL)
    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    @ManyToMany
    @JoinTable(name = "game_genre", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Information> getInformationList() {
        return informationList;
    }

    public void setInformationList(List<Information> informationList) {
        this.informationList = informationList;
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public double getRating() {
        if (reviews == null || reviews.isEmpty()) {
            setRating(0);
        } else {
            double ratingSum = 0;
            int ratingCount = 0;
            for (Review review : reviews) {
                ratingSum += review.getRating();
                ratingCount ++;
            }
            double dynamicRating = ratingSum / ratingCount;
            setRating(dynamicRating);
        }

        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
