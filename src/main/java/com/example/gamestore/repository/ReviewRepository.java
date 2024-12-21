package com.example.gamestore.repository;

import com.example.gamestore.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends BaseRepository<Review, UUID> {

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    List<Review> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Review r WHERE r.game.id = :gameId")
    List<Review> findByGameId(@Param("gameId") UUID gameId);

    @Query("SELECT r FROM Review r WHERE r.dateOfPublication = :dateOfPublication")
    List<Review> findByDate(@Param("dateOfPublication") LocalDate dateOfPublication);

    @Query("SELECT r FROM Review r WHERE r.rating >= :rating")
    List<Review> findByRating(@Param("rating") double rating);
}
