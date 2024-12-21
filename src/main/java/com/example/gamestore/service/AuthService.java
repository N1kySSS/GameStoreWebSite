package com.example.gamestore.service;

import com.example.gamestore.dto.UserRegistrationDTO;
import com.example.gamestore.entity.User;

public interface AuthService {

    void register(UserRegistrationDTO userRegistrationDTO);

    User getUser(String email);
}
