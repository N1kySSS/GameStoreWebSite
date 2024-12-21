package com.example.gamestore.service;

import com.example.gamestore.dto.ReviewDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewDTO addReview(ReviewDTO reviewDTO);

    List<ReviewDTO> getAll();

    ReviewDTO getById(UUID id);

    List<ReviewDTO> getAllByUserId(UUID userId);

    List<ReviewDTO> getByGameId(UUID gameId);

    List<ReviewDTO> getByDate(LocalDate dateOfPublication);

    List<ReviewDTO> getByRating(double rating);

    void deleteReview(UUID id);
}
