package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.DonationController;
import com.example.gameContracts.input.user.DepositInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.page.DonationViewModel;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.UserService;
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

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/donation")
public class DonationControllerImpl implements DonationController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final UserService userService;

    public DonationControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping()
    public String index(Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "Donation page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new DonationViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("form", new DepositInputModel(100));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());

        return "donation";
    }

    @Override
    @PostMapping()
    public String indexAfter(DepositInputModel form, BindingResult bindingResult, Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "Donation request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var countOfMoney = form.countOfMoney() != null ? form.countOfMoney() : 0;
        try {
            if (countOfMoney < 100) {
                throw new RuntimeException("Сумма не может быть меньше 100 рублей");
            }
            userService.updateUserBalance(userDTO.getId(), countOfMoney);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Donation", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new DonationViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("form", form);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("currentUri", request.getRequestURI());

            return "donation";
        }

        return "redirect:/";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }
}
