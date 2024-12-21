package com.example.gamestore.controller;

import com.example.gameContracts.controllers.page.OrderController;
import com.example.gameContracts.input.order.OrderCreateInputModel;
import com.example.gameContracts.input.order.OrderUpdateInputModel;
import com.example.gameContracts.viewModel.base.BaseViewModel;
import com.example.gameContracts.viewModel.order.BaseOrderViewModel;
import com.example.gameContracts.viewModel.order.MyOrderViewModel;
import com.example.gameContracts.viewModel.page.OrderViewModel;
import com.example.gameContracts.viewModel.review.BaseReviewViewModel;
import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.Platform;
import com.example.gamestore.entity.enums.Status;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.OrderService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import static com.example.gamestore.utils.BaseWebSiteHeader.*;

@Controller
@RequestMapping("/order")
public class OrderControllerImpl implements OrderController {
    private static final Logger LOG = LogManager.getLogger(Controller.class);
    private final UserService userService;
    private final OrderService orderService;
    private final GameService gameService;

    public OrderControllerImpl(UserService userService, OrderService orderService, GameService gameService) {
        this.userService = userService;
        this.orderService = orderService;
        this.gameService = gameService;
    }

    @Override
    @GetMapping()
    public String listOrders(Model model, HttpServletRequest request) {
        LOG.log(Level.INFO, "User's orders page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        List<OrderDTO> orderDTOList = orderService.getAllByUserId(userDTO.getId());
        List<MyOrderViewModel> orders = orderDTOList
                .stream()
                .map(it -> new MyOrderViewModel(gameService.getById(it.getGameId()).getName(), it.getGameId(), it.getSum(), it.getStatus().toString()))
                .toList();

        var viewModel = new OrderViewModel(
                createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()),
                orders
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUri", request.getRequestURI());

        return "order";
    }

    @Override
    @GetMapping("/create")
    public String createForm(Model model) {
        LOG.log(Level.INFO, "Order create page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new BaseOrderViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new OrderCreateInputModel(""));

        return "order-create";
    }

    @Override
    @PostMapping("/create")
    public String create(OrderCreateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "Order create request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        UUID gameId = gameService.getByName(form.game()).getId();
        int balance = userService.getById(userDTO.getId()).getBalance();
        int sumOfOrder = gameService.getByName(form.game()).getPrice();
        int newBalance = -sumOfOrder;

        try {
            if (balance >= sumOfOrder) {
                orderService.addOrder(new OrderDTO(UUID.randomUUID(), Status.ADOPTED, sumOfOrder, LocalDate.now(), userDTO.getId(), gameId));
                userService.updateUserBalance(userDTO.getId(), newBalance);
            } else {
                throw new RuntimeException("Недостаточно средств");
            }
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Order", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new BaseOrderViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "order-create";
        }

        return "redirect:/order";
    }

    @Override
    @GetMapping("/update")
    public String updateForm(Model model) {
        LOG.log(Level.INFO, "Order update page request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        var viewModel = new BaseOrderViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

        model.addAttribute("model", viewModel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("form", new OrderUpdateInputModel("", ""));

        return "order-update";
    }

    @Override
    @PostMapping("/update")
    public String update(OrderUpdateInputModel form, BindingResult bindingResult, Model model) {
        LOG.log(Level.INFO, "Order update request " + form);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userService.getByEmail(authentication.getName());

        boolean isAdmin = userDTO.getRoles().stream().anyMatch(role -> role == UserRoles.ADMIN);

        try {
            orderService.update(Status.valueOf(form.status().trim()), UUID.fromString(form.id()));
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("Order", e.getMessage()));
        }

        if (bindingResult.hasErrors()) {
            var viewModel = new BaseOrderViewModel(createBaseViewModel("Главная страница", webSiteName, userDTO.getFirstName(), userDTO.getPicUri(), webSiteLogo, userDTO.getBalance()));

            model.addAttribute("model", viewModel);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("form", form);

            return "order-update";
        }

        return "redirect:/crud";
    }

    @Override
    public BaseViewModel createBaseViewModel(String title, String webSiteName, String userNickname, String userPicUri, String webSitePicUri, Integer balance) {
        return new BaseViewModel(title, webSiteName, userNickname, userPicUri, webSitePicUri, balance);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> all() {
        LOG.log(Level.INFO, "All order request");

        List<OrderDTO> orders = orderService.getAll();
        return ResponseEntity.ok(orders);
    }
}
