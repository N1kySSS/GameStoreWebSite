package com.example.gamestore.utils;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.dto.GenreDTO;
import com.example.gamestore.dto.ReviewDTO;
import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.Platform;
import com.example.gamestore.entity.enums.UserRoles;
import com.example.gamestore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class GameStoreRunner implements CommandLineRunner {
    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private final ConfigurableApplicationContext context;
    private final GameService gameService;
    private final GenreService genreService;
    private final InformationService informationService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public GameStoreRunner(ConfigurableApplicationContext context, GameService gameService, GenreService genreService, InformationService informationService, OrderService orderService, ReviewService reviewService, UserService userService) {
        this.context = context;
        this.gameService = gameService;
        this.genreService = genreService;
        this.informationService = informationService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTED");

        while (true) {
            System.out.println("""
                    Choose option from:\

                    0 - for Exit\

                    1 - for Add Game\

                    2 - for Add Genre\

                    3 - for Find by email\

                    4 - for Add Order\

                    5 - for Add Review\

                    6 - for Add User\

                    7 - for Get all games\

                    8 - for Get all genres\

                    9 - for Get all information\

                    10 - for Get all orders\

                    11 - for Get all review\

                    12 - for Get all users\

                    13 - for Update user password\

                    14 - for Update genre description\

                    15 - for Update game\

                    16 - for Get information by status\

                    17 - for Get order by date\

                    18 - for Get review by gameId \

                    19 - for Get game by price between\

                    20 - for Delete game\

                    21 - for Delete user\

                    22 - for Delete review""");

            String input = bufferedReader.readLine().toLowerCase();

            switch (input) {
                case "0":
                    context.close();
                    return;
                case "1":
                    this.addGame();
                    break;
                case "2":
                    this.addGenre();
                    break;
                case "3":
                    this.findUser();
                    break;
//                case "4":
//                    this.addOrder();
//                    break;
                case "5":
                    this.addReview();
                    break;
                case "6":
                    this.addUser();
                    break;
                case "7":
                    this.getGames();
                    break;
                case "8":
                    this.getGenres();
                    break;
//                case "9":
//                    this.getInformation();
//                    break;
//                case "10":
//                    this.getOrders();
//                    break;
//                case "11":
//                    this.getReview();
//                    break;
                case "12":
                    this.getUsers();
                    break;
//                case "13":
//                    this.updateUserPassword();
//                    break;
//                case "14":
//                    this.updateGenreDescription();
//                    break;
//                case "15":
//                    this.updateGame();
//                    break;
//                case "16":
//                    this.getInformationByStatus();
//                    break;
//                case "17":
//                    this.getOrderByDate();
//                    break;
//                case "18":
//                    this.getReviewByGameId();
//                    break;
//                case "19":
//                    this.getGameByPriceBetween();
//                    break;
//                case "20":
//                    this.deleteGame();
//                    break;
//                case "21":
//                    this.deleteUser();
//                    break;
//                case "22":
//                    this.deleteReview();
//                    break;
                default:
                    System.out.println("Неверная команда");
            }
            System.out.println("==================================");
        }
    }

    private List<UserDTO> getUsers() throws IOException {
        return userService.getAll();
    }

    private void getGames() throws IOException {
        System.out.println(gameService.getById(UUID.fromString("1a999cd8-1728-4e8a-965f-52c21c56a5c4")).getRating());
        System.out.println(gameService.getById(UUID.fromString("5694acc7-560e-4f81-bd1a-610ae49e9b98")).getRating());
    }

    private List<GenreDTO> getGenres() throws IOException {
        return genreService.getAll();
    }

    private void addUser() throws IOException {
        System.out.println("Введите данные пользователя в таком формате: Картинка firstName lastName email password date-of-birthday");
        String[] userParams = bufferedReader.readLine().split("\\s+");

        String[] userDate = userParams[5].split("-");
        int year = Integer.parseInt(userDate[0]);
        int month = Integer.parseInt(userDate[1]);
        int day = Integer.parseInt(userDate[2]);
        UserDTO userDTO = new UserDTO(null, userParams[0], userParams[1], userParams[2], userParams[3], userParams[4], LocalDate.of(year, month, day), Set.of(UserRoles.USER));

        try {
            this.userService.addUser(userDTO);
            System.out.println("Пользователь успешно добавлен");
        } catch (Exception e) {
            System.out.println("Ошибка добавления пользователя: " + e.getMessage());
        }
    }

    private void addGenre() throws IOException {
        System.out.println("Введите данные в таком формате: название описание");
        String[] userParams = bufferedReader.readLine().split("\\s+");
        GenreDTO genreDTO = new GenreDTO(null, userParams[0], userParams[1]);

        try {
            this.genreService.addGenre(genreDTO);
            System.out.println("Жанр успешно добавлен");
        } catch (Exception e) {
            System.out.println("Ошибка добавления жанра: " + e.getMessage());
        }
    }

    private void addGame() throws IOException {
        System.out.println("Введите данные в таком формате: картинка цена рейтинг название разработчик");
        String[] gameParams = bufferedReader.readLine().split("\\s+");
        GameDTO gameDTO = new GameDTO(null, gameParams[0], Integer.parseInt(gameParams[1]), gameParams[3], gameParams[4], null, LocalDate.now(), List.of(Platform.PC.toString(), Platform.Nintendo.toString()), genreService.getAll());

        try {
            this.gameService.addGame(gameDTO);
            System.out.println("Игра успешно добавлен");
        } catch (Exception e) {
            System.out.println("Ошибка добавления игры: " + e.getMessage());
        }
    }

    private void addReview() throws IOException {
        System.out.println("Введите данные в таком формате: рэйтинг(0-5) описание idИгры idПользователя");
        String[] reviewParams = bufferedReader.readLine().split("\\s+");
        ReviewDTO reviewDTO = new ReviewDTO(null, Double.parseDouble(reviewParams[0]), reviewParams[1], LocalDate.now(), UUID.fromString(reviewParams[2]), UUID.fromString(reviewParams[3]));

        try {
            this.reviewService.addReview(reviewDTO);
            System.out.println("Отзыв успешно добавлен");
        } catch (Exception e) {
            System.out.println("Ошибка добавления Отзыва: " + e.getMessage());
        }
    }

    private void findUser() throws IOException {
        System.out.println("Введите почту");
        String[] userParams = bufferedReader.readLine().split("\\s+");

        UserDTO userDTO = userService.getByEmail(userParams[0]);
        System.out.println(userDTO.getId() + userDTO.getEmail() + userDTO.getPassword());
    }

    //TODO(Maybe add method's realization later...)
}
