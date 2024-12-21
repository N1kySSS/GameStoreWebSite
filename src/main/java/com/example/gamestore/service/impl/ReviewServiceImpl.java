package com.example.gamestore.service.impl;

import com.example.gamestore.dto.ReviewDTO;
import com.example.gamestore.entity.Review;
import com.example.gamestore.repository.ReviewRepository;
import com.example.gamestore.service.ReviewService;
import com.example.gamestore.utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableCaching
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(cacheNames = "reviews", allEntries = true)
    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        if (reviewDTO.getGameId() == null) {
            throw new IllegalArgumentException("Game ID cannot be null");
        }

        if (!this.validationUtil.isValid(reviewDTO)) {
            this.validationUtil
                    .violations(reviewDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Данные отзыва не валидны");
        }

        if (reviewDTO == null) {
            throw new RuntimeException("Данные отзыва отсутствуют");
        }
        Review review = modelMapper.map(reviewDTO, Review.class);
        Review savedReview = reviewRepository.save(review);

        return modelMapper.map(savedReview, ReviewDTO.class);
    }

    @Override
    @Cacheable("reviews")
    public List<ReviewDTO> getAll() {
        List<Review> reviews = (List<Review>) reviewRepository.findAll();
        List<ReviewDTO> dtoReviews = new ArrayList<>();
        reviews.forEach(it -> dtoReviews.add(modelMapper.map(it, ReviewDTO.class)));

        if (dtoReviews.isEmpty()) {
            throw new RuntimeException("Отзывы не найдены");
        } else {
            return dtoReviews;
        }
    }

    @Override
    public ReviewDTO getById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            return modelMapper.map(review, ReviewDTO.class);
        } else {
            throw new RuntimeException("Отзыв с таким id: " + id + " не найден");
        }
    }

    @Override
    public List<ReviewDTO> getAllByUserId(UUID userId) {
        if (userId == null) {
            throw new RuntimeException("Неверный id");
        }

        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<ReviewDTO> dtoReviews = new ArrayList<>();
        reviews.forEach(it -> dtoReviews.add(modelMapper.map(it, ReviewDTO.class)));

        return dtoReviews;
    }

    @Override
    public List<ReviewDTO> getByGameId(UUID gameId) {
        if (gameId == null) {
            throw new RuntimeException("Неверный gameId");
        }

        List<Review> reviews = reviewRepository.findByGameId(gameId);
        List<ReviewDTO> dtoReviews = new ArrayList<>();
        reviews.forEach(it -> dtoReviews.add(modelMapper.map(it, ReviewDTO.class)));

        if (dtoReviews.isEmpty()) {
            throw new RuntimeException("У этой игры нет отзывов");
        } else {
            return dtoReviews;
        }
    }

    @Override
    public List<ReviewDTO> getByDate(LocalDate dateOfPublication) {
        if (dateOfPublication == null) {
            throw new RuntimeException("Неверная дата");
        }

        List<Review> reviews = reviewRepository.findByDate(dateOfPublication);
        List<ReviewDTO> dtoReviews = new ArrayList<>();
        reviews.forEach(it -> dtoReviews.add(modelMapper.map(it, ReviewDTO.class)));

        if (dtoReviews.isEmpty()) {
            throw new RuntimeException("Отзывов за эту дату не найдено");
        } else {
            return dtoReviews;
        }
    }

    @Override
    public List<ReviewDTO> getByRating(double rating) {
        List<Review> reviews = reviewRepository.findByRating(rating);
        List<ReviewDTO> dtoReviews = new ArrayList<>();
        reviews.forEach(it -> dtoReviews.add(modelMapper.map(it, ReviewDTO.class)));

        if (dtoReviews.isEmpty()) {
            throw new RuntimeException("Игр с таким рейтингом не найдено");
        } else {
            return dtoReviews;
        }
    }

    @Override
    @CacheEvict(cacheNames = "reviews", allEntries = true)
    public void deleteReview(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (reviewRepository.findById(id).isPresent()) {
            reviewRepository.deleteById(id);
        } else {
            throw new RuntimeException("Отзыва с таким id: " + id + " не существует");
        }
    }
}
