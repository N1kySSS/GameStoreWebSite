package com.example.gamestore.repository;

import com.example.gamestore.entity.Game;
import com.example.gamestore.entity.enums.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends BaseRepository<Game, UUID> {

    @Query("SELECT g FROM Game g JOIN g.genres genre WHERE genre.name = :genreName")
    List<Game> findByGenre(@Param("genreName") String genreName);

    @Query("SELECT g FROM Game g WHERE g.price BETWEEN :startPrice AND :endPrice")
    List<Game> findByPriceBetween(@Param("startPrice") int startPrice, @Param("endPrice") int endPrice);

    List<Game> findTop5ByRatingBetween(@Param("minRating") double minRating, @Param("maxRating") double maxRating);

    @Query("SELECT g FROM Game g WHERE g.developer = :developer")
    List<Game> findByDeveloper(@Param("developer") String developer);

    @Query("SELECT g FROM Game g JOIN g.platforms p WHERE p = :platform")
    List<Game> findByPlatform(@Param("platform") String platform);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.name = :name, g.price = :price, g.description = :description, g.picUri = :picUri, " +
            "g.platforms = :platforms, g.releaseData = :releaseData, g.rating = :rating, g.developer = :developer " +
            "WHERE g.id = :id")
    void updateGame(
            @Param("name") String name,
            @Param("price") int price,
            @Param("description") String description,
            @Param("platforms") List<Platform> platforms,
            @Param("releaseData") LocalDate releaseData,
            @Param("rating") double rating,
            @Param("developer") String developer,
            @Param("picUri") String picUri,
            @Param("id") UUID id
    );

    Page<Game> findByNameContainingIgnoreCase(String string, Pageable pageable);

    Game findByNameIgnoreCase(@Param("name") String name);

    List<Game> findTop5ByOrderByReleaseDataDesc();

    List<Game> findTop5ByOrderByRatingDesc();
}
