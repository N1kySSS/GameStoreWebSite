package com.example.gamestore.service;

import com.example.gamestore.dto.GenreDTO;

import java.util.List;
import java.util.UUID;

public interface GenreService {
    GenreDTO addGenre(GenreDTO genreDTO);

    List<GenreDTO> getAll();

    GenreDTO getById(UUID id);

    GenreDTO getByName(String name);

    void updateGenre(GenreDTO genreDTO);

    void deleteGenre(UUID id);
}
