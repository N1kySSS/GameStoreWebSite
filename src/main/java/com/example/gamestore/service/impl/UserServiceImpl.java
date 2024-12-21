package com.example.gamestore.service.impl;

import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.User;
import com.example.gamestore.repository.UserRepository;
import com.example.gamestore.service.UserService;
import com.example.gamestore.utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ValidationUtil validationUtil, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        if (!this.validationUtil.isValid(userDTO)) {
            this.validationUtil
                    .violations(userDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные пользователя не валидны");
        }

        if (userDTO == null) {
            throw new RuntimeException("Данные пользователя отсутствуют ");
        }
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAll() {
        List<User> users = (List<User>) userRepository.findAll();
        List<UserDTO> dtoUsers = new ArrayList<>();
        users.forEach(it -> dtoUsers.add(modelMapper.map(it, UserDTO.class)));

        if (dtoUsers.isEmpty()) {
            throw new RuntimeException("Пользователи не найдены");
        } else {
            return dtoUsers;
        }
    }

    @Override
    public UserDTO getById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return modelMapper.map(user, UserDTO.class);
        } else {
            throw new RuntimeException("Пользователь с таким id: " + id + " не найден");
        }
    }

    @Override
    public List<UserDTO> getByFirstName(String firstName) {
        if (firstName == null) {
            throw new RuntimeException("Неверное имя");
        }

        List<User> users = userRepository.findByFirstName(firstName);
        List<UserDTO> dtoUsers = new ArrayList<>();
        users.forEach(it -> dtoUsers.add(modelMapper.map(it, UserDTO.class)));

        if (dtoUsers.isEmpty()) {
            throw new RuntimeException("Пользователи с таким именем: " + firstName + " не найдены");
        } else {
            return dtoUsers;
        }
    }

    @Override
    public List<UserDTO> getByLastName(String lastName) {
        if (lastName == null) {
            throw new RuntimeException("Неверная фамилия");
        }

        List<User> users = userRepository.findByLastName(lastName);
        List<UserDTO> dtoUsers = new ArrayList<>();
        users.forEach(it -> dtoUsers.add(modelMapper.map(it, UserDTO.class)));

        if (dtoUsers.isEmpty()) {
            throw new RuntimeException("Пользователи с такой фамилией: " + lastName + " не найдены");
        } else {
            return dtoUsers;
        }
    }

    @Override
    public UserDTO getByEmail(String email) {
        if (email == null) {
            throw new RuntimeException("Неверный email");
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с такой почтой: " + email + " не найден");
        } else {
            return modelMapper.map(user, UserDTO.class);
        }
    }

    @Override
    public void updateUserInfo(UserDTO userDTO) {
        if (!this.validationUtil.isValid(userDTO)) {
            this.validationUtil
                    .violations(userDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные пользователя не валидны");
        }

        if (userDTO == null) {
            throw new RuntimeException("Данные пользователя отсутствуют");
        }

        Optional<User> existingGenre = userRepository.findById(userDTO.getId());

        if (existingGenre.isPresent()) {
            User user = modelMapper.map(userDTO, User.class);
            userRepository.updateDateOfBirthday(user.getDateOfBirthday(), user.getId());
            userRepository.updateFirstName(user.getFirstName(), user.getId());
            userRepository.updateLastName(user.getLastName(), user.getId());
            userRepository.updatePicUri(user.getPicUri(), user.getId());
        } else {
            throw new RuntimeException("Пользователя с таким id: " + userDTO.getId() + " не существует");
        }
    }

    @Override
    public void updateUserBalance(UUID userId, int balance) {
        if (userId == null) {
            throw new RuntimeException("Данные пользователя отсутствуют");
        }

        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            userRepository.updateBalance(existingUser.get().getBalance() + balance, userId);
        } else {
            throw new RuntimeException("Пользователя с таким id: " + userId + " не существует");
        }
    }

    @Override
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Пользователя с таким id: " + id + " не существует");
        }
    }
}
