package com.example.gamestore.service;


import com.example.gamestore.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO addUser(UserDTO userDTO);

    List<UserDTO> getAll();

    UserDTO getById(UUID id);

    UserDTO getByEmail(String email);

    void updateUserInfo(UserDTO userDTO);

    void updateUserBalance(UUID userId, int balance);

    void deleteUser(UUID id);
}
