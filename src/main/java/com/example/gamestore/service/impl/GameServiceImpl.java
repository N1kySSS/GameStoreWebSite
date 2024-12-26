package com.example.gamestore.service.impl;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.entity.Game;
import com.example.gamestore.entity.enums.Platform;
import com.example.gamestore.repository.GameRepository;
import com.example.gamestore.service.GameService;
import com.example.gamestore.utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "allGames", allEntries = true)
    public GameDTO addGame(GameDTO gameDTO) {
        if (!this.validationUtil.isValid(gameDTO)) {
            this.validationUtil
                    .violations(gameDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные игры не валидны");
        }

        if (gameDTO == null) {
            throw new RuntimeException("Данные игры отсутствуют ");
        }
        Game game = modelMapper.map(gameDTO, Game.class);
        Game savedGame = gameRepository.save(game);

        return modelMapper.map(savedGame, GameDTO.class);
    }

    @Override
    @Transactional
    @Cacheable("allGames")
    public List<GameDTO> getAll() {
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GameDTO> dtoGames = new ArrayList<>();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        if (dtoGames.isEmpty()) {
            throw new RuntimeException("Игры не найдены");
        } else {
            return dtoGames;
        }
    }

    @Override
    @Transactional
    public GameDTO getById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<Game> game = gameRepository.findById(id);

        if (game.isPresent()) {
            return modelMapper.map(game, GameDTO.class);
        } else {
            throw new RuntimeException("Игра с таким id: " + id + " не найдена");
        }
    }

    @Override
    public List<GameDTO> getByGenre(String genreName) {
        if (genreName == null) {
            throw new RuntimeException("Неверное имя");
        }

        List<Game> games = gameRepository.findByGenre(genreName);
        List<GameDTO> dtoGames = new ArrayList<>();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        if (dtoGames.isEmpty()) {
            throw new RuntimeException("Игры с таким жанром: " + genreName + " не найдены");
        } else {
            return dtoGames;
        }
    }

    @Override
    public List<GameDTO> getByPriceBetween(int startPrice, int endPrice) {
        List<Game> games = gameRepository.findByPriceBetween(startPrice, endPrice);
        List<GameDTO> dtoGames = new ArrayList<>();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        if (dtoGames.isEmpty()) {
            throw new RuntimeException("Игр в таком ценовом диапазоне [" + startPrice + " - " + endPrice + "] нет");
        } else {
            return dtoGames;
        }
    }

    @Override
    public List<GameDTO> findByRatingBetween(double minRating, double maxRating) {
        List<Game> games = gameRepository.findTop5ByRatingBetween(minRating, maxRating);
        List<GameDTO> dtoGames = new ArrayList<>();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        return dtoGames;

    }

    @Override
    @Transactional
    public GameDTO getByName(String name) {
        if (name == null) {
            throw new RuntimeException("Неверное название");
        }

        Game game = gameRepository.findByNameIgnoreCase(name);
        GameDTO dtoGame = modelMapper.map(game, GameDTO.class);

        if (dtoGame == null) {
            throw new RuntimeException("Игры с таким именем: " + name + " не существует");
        } else {
            return dtoGame;
        }
    }

    @Override
    public List<GameDTO> getByDeveloper(String developer) {
        if (developer == null) {
            throw new RuntimeException("Неверный издатель");
        }

        List<Game> games = gameRepository.findByDeveloper(developer);
        List<GameDTO> dtoGames = new ArrayList<>();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        if (dtoGames.isEmpty()) {
            throw new RuntimeException("Игры с таким издателем: " + developer + " не найдены");
        } else {
            return dtoGames;
        }
    }

    @Override
    public List<GameDTO> getByPlatform(String platform) {
        if (platform == null) {
            throw new RuntimeException("Неверный email");
        }

        List<Game> games = gameRepository.findByPlatform(platform);
        List<GameDTO> dtoGames = new ArrayList<>();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        if (dtoGames.isEmpty()) {
            throw new RuntimeException("Игры на такой платформе: " + platform + " не найдены");
        } else {
            return dtoGames;
        }
    }

    @Override
    public List<GameDTO> findNew() {
        List<GameDTO> dtoGames = new ArrayList<>();

        List<Game> games = gameRepository.findTop5ByOrderByReleaseDataDesc();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        return dtoGames;
    }

    @Override
    public List<GameDTO> findPopular() {
        List<GameDTO> dtoGames = new ArrayList<>();

        List<Game> games = gameRepository.findTop5ByOrderByRatingDesc();
        games.forEach(it -> dtoGames.add(modelMapper.map(it, GameDTO.class)));

        return dtoGames;
    }

    @Override
    public Page<GameDTO> getGames(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name"));
        Page<Game> gamesPage = searchTerm != null
                ? gameRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                : (Page<Game>) gameRepository.findAll();

        return gamesPage.map(game -> new GameDTO(game.getId(), game.getPicUri(), game.getPrice(), game.getName(), game.getDeveloper(), game.getDescription(), game.getReleaseData(), game.getPlatforms().stream().map(Platform::toString).toList(), game.getGenres().stream().map(it -> modelMapper.map(it, GenreDTO.class)).toList()));
    }

    @Override
    @CacheEvict(cacheNames = "allGames", allEntries = true)
    public void updateGame(GameDTO gameDTO) {
        if (!this.validationUtil.isValid(gameDTO)) {
            this.validationUtil
                    .violations(gameDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные игры не валидны");
        }

        if (gameDTO == null) {
            throw new RuntimeException("Данные игры отсутствуют");
        }

        Optional<Game> existingGenre = gameRepository.findById(gameDTO.getId());

        if (existingGenre.isPresent()) {
            Game game = modelMapper.map(gameDTO, Game.class);
            gameRepository.updateGame(
                    game.getName(),
                    game.getPrice(),
                    game.getDescription(),
                    game.getPlatforms(),
                    game.getReleaseData(),
                    game.getRating(),
                    game.getDeveloper(),
                    game.getPicUri(),
                    game.getId()
            );
        } else {
            throw new RuntimeException("Игры с таким id: " + gameDTO.getId() + " не существует");
        }
    }

    @Override
    @CacheEvict(cacheNames = "allGames", allEntries = true)
    public void deleteGame(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (gameRepository.findById(id).isPresent()) {
            gameRepository.deleteById(id);
        } else {
            throw new RuntimeException("Игры с таким id: " + id + " не существует");
        }
    }
}
