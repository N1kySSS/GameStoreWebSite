package com.example.gamestore.repository;

import com.example.gamestore.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends BaseRepository<Review, UUID> {

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    List<Review> findByUserId(@Param("userId") UUID userId);
}
