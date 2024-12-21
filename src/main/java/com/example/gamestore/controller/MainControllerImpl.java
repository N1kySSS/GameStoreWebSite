package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.MainController;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.game.GameViewModel;
import com.example.gameContracts.viewModel.page.MainViewModel;
import com.example.gamestore.dto.GameDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/")
public class MainControllerImpl implements MainController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final GameService gameService;
    private final UserService userService;

    public MainControllerImpl(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "Main page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        List<GameDTO> popularGameDTOS = gameService.findPopular();
        List<GameViewModel> popularGames;

        if (popularGameDTOS.isEmpty()) {
            popularGames = new ArrayList<>();
        } else {
            popularGames = popularGameDTOS.
                    stream()
                    .map(dto -> new GameViewModel(
                            dto.getId(),
                            dto.getName(),
                            dto.getPicUri(),
                            dto.getPrice()
                    ))
                    .toList();
        }

        List<GameDTO> newGameDTOS = gameService.findNew();
        List<GameViewModel> newGames;

        if (newGameDTOS.isEmpty()) {
            newGames = new ArrayList<>();
        } else {
            newGames = newGameDTOS
                    .stream()
                    .map(dto -> new GameViewModel(
                            dto.getId(),
                            dto.getName(),
                            dto.getPicUri(),
                            dto.getPrice()
                    ))
                    .toList();
        }

        var viewModel = new MainViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()),
                popularGames,
                newGames
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());

        return "index";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }
}
