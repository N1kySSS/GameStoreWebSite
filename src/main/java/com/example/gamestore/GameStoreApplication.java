package com.example.gamestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GameStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(GameStoreApplication.class, args);
	}
}
