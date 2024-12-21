package com.example.gamestore.service.impl;

import com.example.gamestore.dto.UserRegistrationDTO;
import com.example.gamestore.entity.User;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.repository.UserRepository;
import com.example.gamestore.service.AuthService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRegistrationDTO userRegistrationDTO) {
        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }

        Optional<User> userByEmail = this.userRepository.findByEmail(userRegistrationDTO.getEmail());

        if (userByEmail.isPresent()) {
            throw new RuntimeException("пользователь с такой почтой уже зарегистрирован");
        }

        User user = new User(
                userRegistrationDTO.getFirstName(),
                userRegistrationDTO.getLastName(),
                userRegistrationDTO.getEmail(),
                passwordEncoder.encode(userRegistrationDTO.getPassword())
        );

        user.setRoles(Set.of(UserRoles.USER));

        this.userRepository.save(user);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " was not found!"));
    }
}
