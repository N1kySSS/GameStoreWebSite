package com.example.gamestore.controller;

import com.example.gameContracts.controllers.genre.GenreController;
import com.example.gameContracts.input.genre.GenreCreateUpdateInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.base.DefaultViewModel;
import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.GenreService;
import com.example.gamestore.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/genres")
public class GenreControllerImpl implements GenreController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final GenreService genreService;
    private final UserService userService;

    public GenreControllerImpl(GenreService genreService, UserService userService) {
        this.genreService = genreService;
        this.userService = userService;
    }

    @Override
    @GetMapping("/createUpdate")
    public String createForm(Model model) {
        LOG.log(Level.INFO, "Genre create/update page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new DefaultViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new GenreCreateUpdateInputModel("", "", ""));

        return "genre-createUpdate";
    }

    @PostMapping("/createUpdate")
    public String create(GenreCreateUpdateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "Genre create/update request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        try {
            if (form.genreId() == null || form.genreId().isBlank()) {
                genreService.addGenre(new GenreDTO(UUID.randomUUID(), form.name(), form.description()));
            } else {
                GenreDTO genreDTO = genreService.getById(UUID.fromString(form.genreId()));
                genreDTO.setDescription(form.description());
                genreDTO.setName(form.name());
                genreService.updateGenre(genreDTO);
            }
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Genre", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new DefaultViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "genre-createUpdate";
        }

        return "redirect:/crud";
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<GenreDTO>> all() {
        LOG.log(Level.INFO, "All genre page request");

        List<GenreDTO> genres = genreService.getAll();
        return ResponseEntity.ok(genres);
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }
}
