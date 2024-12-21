package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.CategoryController;
import com.example.gameContracts.input.game.GameSearchInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.game.DetailedGameViewModel;
import com.example.gameContracts.viewModel.game.GameViewModel;
import com.example.gameContracts.viewModel.page.CategoryViewModel;
import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/category")
public class CategoryControllerImpl implements CategoryController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final GameService gameService;
    private final UserService userService;

    public CategoryControllerImpl(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    @GetMapping()
    public String listGames(@ModelAttribute("form") GameSearchInputModel inputModel, Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "List of games with searchTerm " + inputModel);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var searchTerm = inputModel.searchTerm() != null ? inputModel.searchTerm() : "";
        var page = inputModel.page() != null ? inputModel.page() : 1;
        var size = inputModel.size() != null ? inputModel.size() : 3;
        var genre = inputModel.genre() != null ? inputModel.genre() : "";
        var platform = inputModel.platform() != null ? inputModel.platform() : "";
        var rating = inputModel.rating() != null ? inputModel.rating() : 0;
        var minPrice = inputModel.minPrice() != null ? inputModel.minPrice() : 0;
        var maxPrice = inputModel.maxPrice() != null ? inputModel.maxPrice() : Integer.MAX_VALUE;
        var sortBy = inputModel.sortBy() != null ? inputModel.sortBy() : "";
        inputModel = new GameSearchInputModel(searchTerm, genre, platform, rating, minPrice, maxPrice, sortBy, page, size);

        var gamesPage = gameService.getGames(searchTerm, page, size);
        var gameViewModels = gamesPage
                .stream()
                .map(it -> {
                            List<GenreDTO> genreDTOS = it.getGenres();
                            List<String> genres = (genreDTOS != null)
                                    ? genreDTOS.stream()
                                    .map(GenreDTO::getName)
                                    .toList()
                                    : Collections.emptyList();
                            String genresString = String.join(", ", genres);

                            List<String> platformList = it.getPlatforms();
                            String platformsString = "";
                            if (platformList != null) {
                                platformsString = String.join(", ", platformList);
                            }

                            return new DetailedGameViewModel(
                                    new GameViewModel(it.getId(), it.getName(), it.getPicUri(), it.getPrice()),
                                    it.getRating(), genresString, platformsString, it.getDescription()
                            );
                        }
                )
                .toList();

        var viewModel = new CategoryViewModel(
                createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()),
                gameViewModels, gamesPage.getTotalPages()
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("form", inputModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());

        return "category";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }
}
