package com.example.gamestore.dto;

import com.example.gamestore.entity.enums.UserRoles;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserDTO {
    private UUID id;

    private int balance;

    private String picUri;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private LocalDate dateOfBirthday;

    private List<OrderDTO> orders;

    private List<ReviewDTO> reviews;

    private Set<UserRoles> roles;

    public UserDTO(UUID id, String picUri, String firstName, String lastName, String email, String password, LocalDate dateOfBirthday, Set<UserRoles> roles) {
        this.picUri = picUri;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirthday = dateOfBirthday;
        this.orders = null;
        this.reviews = null;
        this.balance = 0;
        this.roles = roles;
    }

    protected UserDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @NotNull
    @NotEmpty
    @Length(min = 3, message = "First name must be minimum three characters")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    @NotEmpty
    @Length(min = 3, message = "Last name must be minimum three characters")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @NotEmpty
    @Length(min = 8, message = "Password must be minimum eight characters")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(LocalDate dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    public Set<UserRoles> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRoles> roles) {
        this.roles = roles;
    }
}
