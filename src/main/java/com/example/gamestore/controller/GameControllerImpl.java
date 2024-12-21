package com.example.gamestore.controller;

import com.example.gameContracts.controllers.game.GameController;
import com.example.gameContracts.input.game.GameCreateUpdateInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.base.DefaultViewModel;
import com.example.gameContracts.viewModel.game.DetailedGameViewModel;
import com.example.gameContracts.viewModel.game.GameViewModel;
import com.example.gameContracts.viewModel.page.DetailsViewModel;
import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.Platform;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.GenreService;
import com.example.gamestore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/games")
public class GameControllerImpl implements GameController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private GameService gameService;
    private final UserService userService;
    private final GenreService genreService;
    private final ModelMapper modelMapper;

    public GameControllerImpl(GameService gameService, UserService userService, GenreService genreService, ModelMapper modelMapper) {
        this.gameService = gameService;
        this.userService = userService;
        this.genreService = genreService;
        this.modelMapper = modelMapper;
    }

    @Override
    @GetMapping("/{id}")
    public String details(@PathVariable String id, Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "Game details page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var game = gameService.getById(UUID.fromString(id));
        List<GenreDTO> genreDTOS = game.getGenres();
        List<String> genres = genreDTOS
                .stream()
                .map(GenreDTO::getName)
                .toList();
        String genresString = String.join(", ", genres);

        List<String> platformList = game.getPlatforms();
        String platformsString = String.join(", ", platformList);

        var viewModel = new DetailsViewModel(
                createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()),
                new DetailedGameViewModel(
                        new GameViewModel(game.getId(), game.getName(), game.getPicUri(), game.getPrice()),
                        game.getRating(), genresString, platformsString, game.getDescription()
                )
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());

        return "game-details";
    }

    @Override
    @GetMapping("/createUpdate")
    public String createForm(Model model) {
        LOG.log(Level.INFO, "Game create/update page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new DefaultViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new GameCreateUpdateInputModel(0, "", "", "", "", "", "", ""));

        return "game-createUpdate";
    }

    @PostMapping("/createUpdate")
    public String create(GameCreateUpdateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "Game create/update request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        try {
            List<String> platforms = Arrays.stream(form.platforms().split(",")).toList();
            List<Platform> platformList = platforms.stream()
                    .map(platform -> {
                        try {
                            return Platform.valueOf(platform.trim());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            List<String> platformsString = platformList.stream().map(Platform::toString).toList();

            List<String> genres = Arrays.stream(form.genres().split(",")).toList();
            List<GenreDTO> genreDTOS = genreService.getAll();
            List<GenreDTO> genreList = genres.stream()
                    .map(genreName -> genreDTOS.stream()
                            .filter(genreDTO -> genreDTO.getName().equalsIgnoreCase(genreName.trim()))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (form.gameId() == null || form.gameId().isBlank()) {
                gameService.addGame(new GameDTO(UUID.randomUUID(), form.imageUrl(), form.price(), form.name(), form.developer(), form.description(), LocalDate.now(), platformsString, genreList));
            } else {
                GameDTO gameDTO = gameService.getById(UUID.fromString(form.gameId()));
                gameDTO.setDescription(form.description());
                gameDTO.setDeveloper(form.developer());
                gameDTO.setPrice(form.price());
                gameDTO.setName(form.name());
                gameDTO.setPicUri(form.imageUrl());
                gameDTO.setPlatforms(platformsString);
                gameDTO.setGenres(genreList);
                gameService.updateGame(gameDTO);
            }
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Game", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new DefaultViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "game-createUpdate";
        }

        return "redirect:/crud";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<GameDTO>> all() {
        LOG.log(Level.INFO, "All game page request");

        List<GameDTO> games = gameService.getAll();
        return ResponseEntity.ok(games);
    }
}
