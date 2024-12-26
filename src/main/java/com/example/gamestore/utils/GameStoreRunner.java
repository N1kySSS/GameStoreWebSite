package com.example.gamestore.utils;

import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.entity.enums.Platform;
import com.example.gamestore.service.GameService;
import com.example.gamestore.service.GenreService;
import com.example.gamestore.service.UserService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class GameStoreRunner implements CommandLineRunner {
    private static final String[] DESCRIPTIONS = {
            "Experience an unforgettable adventure.",
            "Challenge yourself in a world of endless possibilities.",
            "Explore stunning landscapes and unique environments.",
            "Engage in thrilling action and strategic gameplay.",
            "Uncover hidden secrets and solve intriguing mysteries.",
            "Build, create, and shape your own destiny.",
            "Face formidable challenges and emerge victorious.",
            "Immerse yourself in a captivating story.",
            "Discover new worlds and exciting opportunities.",
            "Customize your experience and master your skills.",
            "Collaborate or compete with others in dynamic scenarios.",
            "Test your reflexes and tactical thinking.",
            "Push your limits in a fast-paced, exhilarating journey.",
            "Make choices that shape the outcome of your adventure.",
            "Enjoy a perfect blend of strategy and excitement.",
            "Dive into a richly detailed and immersive experience.",
            "Unleash your creativity in an open-ended world.",
            "Forge your path and leave a lasting impact.",
            "Explore diverse challenges and rewarding gameplay.",
            "Enjoy a timeless experience filled with surprises."
    };
    private static final String baseGameAvatar = "https://yt3.googleusercontent.com/XaT4vQunyDlmIRDsuGqKf9GHRdldFwDyYbSVOJsI21mLTSWnn039ep68KwKe0mv0kFwp9Ge8=s900-c-k-c0x00ffffff-no-rj";
    private static final List<Platform> platforms = Arrays.asList(Platform.values());
    private static final int countOfGenerationAttributesForGame = 2;
    private static final int countOfGeneration = 1;
    Random random = new Random();
    Faker faker = new Faker();

    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private final ConfigurableApplicationContext context;
    private final GenreService genreService;
    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public GameStoreRunner(ConfigurableApplicationContext context, GameService gameService, GenreService genreService, UserService userService) {
        this.context = context;
        this.gameService = gameService;
        this.genreService = genreService;
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

                    3 - for Find user by email""");

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
                default:
                    System.out.println("Неверная команда");
            }
            System.out.println("==================================");
        }
    }

    private void addGame() {
        System.out.println("Later");
    }

    private void addGenre() {
        System.out.println("Later");
    }

    private void findUser() throws IOException {
        System.out.println("Введите почту");
        String[] userParams = bufferedReader.readLine().split("\\s+");

        try {
            UserDTO userDTO = userService.getByEmail(userParams[0]);
            System.out.println(userDTO.getId() + userDTO.getEmail() + userDTO.getPassword());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
