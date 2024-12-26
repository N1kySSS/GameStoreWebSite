package com.example.gamestore.service;

import com.example.gamestore.dto.GameDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface GameService {
    GameDTO addGame(GameDTO gameDTO);

    List<GameDTO> getAll();

    GameDTO getById(UUID id);

    GameDTO getByName(String name);

    List<GameDTO> findNew();

    List<GameDTO> findPopular();

    Page<GameDTO> getGames(String searchTerm, int page, int size);

    void updateGame(GameDTO gameDTO);

    void deleteGame(UUID id);
}
