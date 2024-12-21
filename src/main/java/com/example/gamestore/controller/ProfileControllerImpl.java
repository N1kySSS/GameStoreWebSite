package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.ProfileController;
import com.example.gameContracts.input.user.UserUpdateInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.page.BaseProfileViewModel;
import com.example.gamestore.dto.ReviewDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.UserRoles;
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

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/profile")
public class ProfileControllerImpl implements ProfileController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final UserService userService;

    public ProfileControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping()
    public String index(Model model) {
        LOG.log(Level.INFO, "Profile page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new BaseProfileViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new UserUpdateInputModel(userDTO.getPicUri(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getDateOfBirthday(), userDTO.getId().toString(), ""));

        return "profile";
    }

    @Override
    @PostMapping()
    public String indexUpdate(UserUpdateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "Profile update request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        userDTO.setDateOfBirthday(form.dateOfBirthday());
        userDTO.setFirstName(form.firstName());
        userDTO.setLastName(form.lastName());
        userDTO.setPicUri(form.picUri());

        try {
            userService.updateUserInfo(userDTO);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Profile", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new BaseProfileViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "profile";
        }

        return "redirect:/";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> all() {
        LOG.log(Level.INFO, "All user page request");

        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok(users);
    }
}
