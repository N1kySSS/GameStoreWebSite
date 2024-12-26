package com.example.gamestore.service;

import com.example.gamestore.dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewDTO addReview(ReviewDTO reviewDTO);

    List<ReviewDTO> getAll();

    ReviewDTO getById(UUID id);

    List<ReviewDTO> getAllByUserId(UUID userId);

    void deleteReview(UUID id);
}
