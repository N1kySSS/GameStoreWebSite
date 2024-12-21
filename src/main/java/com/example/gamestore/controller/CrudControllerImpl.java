package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.CrudController;
import com.example.gameContracts.input.base.BaseDeleteInputModel;
import com.example.gameContracts.input.base.BaseReadInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.page.CrudViewModel;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/crud")
public class CrudControllerImpl implements CrudController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final GameService gameService;
    private final GenreService genreService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserService userService;

    public CrudControllerImpl(GameService gameService, GenreService genreService, OrderService orderService, ReviewService reviewService, UserService userService) {
        this.gameService = gameService;
        this.genreService = genreService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @Override
    @GetMapping()
    public String crudInfo(Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "Crud info request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new CrudViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("deleteForm", new BaseDeleteInputModel("", ""));
        model.addAttribute("readForm", new BaseReadInputModel(""));

        return "crud";
    }

    @Override
    @PostMapping("/read")
    public String read(BaseReadInputModel readInputModel, BindingResult bindingResult, HttpServletRequest request, Model model) {
        LOG.log(Level.INFO, "Read request " + readInputModel);

        switch (readInputModel.className().toLowerCase()) {
            case "game":
                return "redirect:/games/all";
            case "genre":
                return "redirect:/genres/all";
            case "order":
                return "redirect:/order/all";
            case "review":
                return "redirect:/review/all";
            case "user":
                return "redirect:/profile/all";
            default:
                bindingResult.addError(new ObjectError("ClassName", "Неверное название класса"));
        }

        if (bindingResult.hasErrors()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDTO userDTO = userService.getByEmail(authentication.getName());

            boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

            var viewModel = new CrudViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("currentUri", "/crud");
            model.addAttribute("deleteForm", new BaseDeleteInputModel("", ""));
            model.addAttribute("readForm", readInputModel);

            return "crud";
        }

        return "redirect:/crud";
    }

    @Override
    @PostMapping("/delete")
    public String delete(BaseDeleteInputModel deleteInputModel, BindingResult bindingResult, HttpServletRequest request, Model model) {
        LOG.log(Level.INFO, "Delete request " + deleteInputModel);

        try {
            switch (deleteInputModel.deleteClassName().toLowerCase()) {
                case "game":
                    gameService.deleteGame(UUID.fromString(deleteInputModel.entityId()));
                    break;
                case "genre":
                    genreService.deleteGenre(UUID.fromString(deleteInputModel.entityId()));
                    break;
                case "order":
                    orderService.deleteOrder(UUID.fromString(deleteInputModel.entityId()));
                    break;
                case "review":
                    reviewService.deleteReview(UUID.fromString(deleteInputModel.entityId()));
                    break;
                case "user":
                    userService.deleteUser(UUID.fromString(deleteInputModel.entityId()));
                    break;
                default:
                    bindingResult.addError(new ObjectError("ClassName", "Неверное название класса"));
            }
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Id", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDTO userDTO = userService.getByEmail(authentication.getName());

            boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

            var viewModel = new CrudViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("currentUri", "/crud");
            model.addAttribute("deleteForm", deleteInputModel);
            model.addAttribute("readForm", new BaseReadInputModel(""));

            return "crud";
        }

        return "redirect:/crud";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }
}
