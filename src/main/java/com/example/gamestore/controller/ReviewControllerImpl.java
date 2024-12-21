package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.ReviewController;
import com.example.gameContracts.input.review.ReviewCreateInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.page.ReviewViewModel;
import com.example.gameContracts.viewModel.review.BaseReviewViewModel;
import com.example.gameContracts.viewModel.review.MyReviewViewModel;
import com.example.gamestore.dto.ReviewDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.ReviewService;
import com.example.gamestore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.time.LocalDate;
import java.util.List;

import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteLogo;
import static com.example.gamestore.utils.BaseWebSiteHeader.webSiteName;

@Controller
@RequestMapping("/review")
public class ReviewControllerImpl implements ReviewController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final UserService userService;
    private final GameService gameService;
    private final ReviewService reviewService;

    public ReviewControllerImpl(UserService userService, GameService gameService, ReviewService reviewService) {
        this.userService = userService;
        this.gameService = gameService;
        this.reviewService = reviewService;
    }

    @Override
    @GetMapping()
    public String listReviews(Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "User's reviews page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        List<ReviewDTO> reviewDTOList = userService.getById(userDTO.getId()).getReviews();
        List<MyReviewViewModel> reviewViewModels = reviewDTOList
                .stream()
                .map(dto -> new MyReviewViewModel(
                                dto.getRating(),
                                dto.getDescription(),
                                gameService.getById(dto.getGameId()).getName()
                        )
                )
                .toList();

        var viewModel = new ReviewViewModel(
                createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()),
                reviewViewModels
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());

        return "review";
    }

    @Override
    @GetMapping("/create")
    public String createForm(Model model) {
        LOG.log(Level.INFO, "Review create page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new BaseReviewViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new ReviewCreateInputModel(0, "", ""));

        return "review-create";
    }

    @Override
    @PostMapping("/create")
    public String create(ReviewCreateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "Review create request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        try {
            reviewService.addReview(new ReviewDTO(null, form.rating(), form.description(), LocalDate.now(), gameService.getByName(form.gameName()).getId(), userDTO.getId()));
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Review", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new BaseReviewViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "review-create";
        }

        return "redirect:/review";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<ReviewDTO>> all() {
        LOG.log(Level.INFO, "All review page request");

        List<ReviewDTO> reviews = reviewService.getAll();
        return ResponseEntity.ok(reviews);
    }
}
