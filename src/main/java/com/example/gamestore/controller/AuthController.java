package com.example.gamestore.controller;

import com.example.gameContracts.controllers.base.BaseController;
import com.example.gameContracts.input.user.UserUpdateInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.base.DefaultViewModel;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.dto.UserRegistrationDTO;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.UserService;
import com.example.gamestore.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/users")
public class AuthController implements BaseController {
    private final AuthServiceImpl authService;
    private final UserService userService;
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    @Autowired
    public AuthController(AuthServiceImpl authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDTO initForm() {
        return new UserRegistrationDTO();
    }

    @GetMapping("/register")
    public String register() {
        LOG.log(Level.INFO, "User want to register");

        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid UserRegistrationDTO userRegistrationDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        LOG.log(Level.INFO, "User params " + userRegistrationDto);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationDto", bindingResult);

            return "redirect:/users/register";
        }

        try {
            this.authService.register(userRegistrationDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            bindingResult.addError(new ObjectError("Register", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationDto", bindingResult);

            return "redirect:/users/register";
        }

        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String login() {
        LOG.log(Level.INFO, "User want to sign in");

        return "login";
    }

    @PostMapping("/login-error")
    public String onFailedLogin(@ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        redirectAttributes.addFlashAttribute("badCredentials", true);

        return "redirect:/users/login";
    }

    @GetMapping("/update")
    public String updateForm(Model model) {
        LOG.log(Level.INFO, "Update user info");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new DefaultViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new UserUpdateInputModel("", "", "", LocalDate.now(), "", ""));

        return "user-update";
    }

    @PostMapping("/update")
    public String update(UserUpdateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "User update params " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        try {
            UserRoles userRole = UserRoles.USER;

            if (form.role().equalsIgnoreCase("admin")) {
                userRole = UserRoles.ADMIN;
            }

            UserDTO updatableUser = userService.getById(UUID.fromString(form.userId()));
            updatableUser.setPicUri(form.picUri());
            updatableUser.setFirstName(form.firstName());
            updatableUser.setLastName(form.lastName());
            updatableUser.setRoles(Set.of(userRole));
            updatableUser.setDateOfBirthday(form.dateOfBirthday());

            userService.updateUserInfo(updatableUser);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("User", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new DefaultViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "user-update";
        }

        return "redirect:/crud";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }
}
