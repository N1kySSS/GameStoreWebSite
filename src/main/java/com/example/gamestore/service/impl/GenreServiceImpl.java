package com.example.gamestore.service.impl;

import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.entity.Genre;
import com.example.gamestore.repository.GenreRepository;
import com.example.gamestore.service.GenreService;
import com.example.gamestore.utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.genreRepository = genreRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public GenreDTO addGenre(GenreDTO genreDTO) {
        if (!this.validationUtil.isValid(genreDTO)) {
            this.validationUtil
                    .violations(genreDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные жанра не валидны");
        }

        if (genreDTO == null) {
            throw new RuntimeException("Данные жанра отсутствуют");
        }

        Genre genre = modelMapper.map(genreDTO, Genre.class);
        Genre savedGenre = genreRepository.save(genre);

        return modelMapper.map(savedGenre, GenreDTO.class);
    }

    @Override
    @Transactional
    public List<GenreDTO> getAll() {
        List<Genre> genres = (List<Genre>) genreRepository.findAll();
        List<GenreDTO> dtoGenres = new ArrayList<>();
        genres.forEach(it -> dtoGenres.add(modelMapper.map(it, GenreDTO.class)));

        if (dtoGenres.isEmpty()) {
            throw new RuntimeException("Жанры не найдены");
        } else {
            return dtoGenres;
        }
    }

    @Override
    public GenreDTO getById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<Genre> genre = genreRepository.findById(id);

        if (genre.isPresent()) {
            return modelMapper.map(genre, GenreDTO.class);
        } else {
            throw new RuntimeException("Жанр с таким id: " + id + " не найден");
        }
    }

    @Override
    public GenreDTO getByName(String name) {
        if (name == null) {
            throw new RuntimeException("Неверное имя");
        }

        Genre genre = genreRepository.findByName(name);

        if (genre == null) {
            throw new RuntimeException("Жанр с таким именем: " + name + " не найден");
        } else {
            return modelMapper.map(genre, GenreDTO.class);
        }
    }

    @Override
    public void updateGenre(GenreDTO genreDTO) {
        if (!this.validationUtil.isValid(genreDTO)) {
            this.validationUtil
                    .violations(genreDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные жанра не валидны");
        }

        if (genreDTO == null) {
            throw new RuntimeException("Данные жанра отсутствуют");
        }

        if (genreDTO.getId() == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<Genre> existingGenre = genreRepository.findById(genreDTO.getId());

        if (existingGenre.isPresent()) {
            Genre genre = modelMapper.map(genreDTO, Genre.class);
            genreRepository.save(genre);
        } else {
            throw new RuntimeException("Жанра с таким id: " + genreDTO.getId() + " не существует");
        }
    }

    @Override
    public void deleteGenre(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (genreRepository.findById(id).isPresent()) {
            genreRepository.deleteById(id);
        } else {
            throw new RuntimeException("Жанра с таким id: " + id + " не существует");
        }
    }
}
