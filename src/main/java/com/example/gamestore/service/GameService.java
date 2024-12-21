package com.example.gamestore.service;

import com.example.gamestore.dto.GameDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface GameService {
    GameDTO addGame(GameDTO gameDTO);

    List<GameDTO> getAll();

    GameDTO getById(UUID id);

    List<GameDTO> getByGenre(String genreName);

    List<GameDTO> getByPriceBetween(int startPrice, int endPrice);

    List<GameDTO> findByRatingBetween(double minRating, double maxRating);

    GameDTO getByName(String name);

    List<GameDTO> getByDeveloper(String developer);

    List<GameDTO> getByPlatform(String platform);

    List<GameDTO> findNew();

    List<GameDTO> findPopular();

    Page<GameDTO> getGames(String searchTerm, int page, int size);

    void updateGame(GameDTO gameDTO);

    void deleteGame(UUID id);
}
