package com.fb.backend.services;

import com.fb.backend.domain.ReviewCreateUpdateRequest;
import com.fb.backend.domain.entities.Review;
import com.fb.backend.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {
    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request);
    Page<Review> listReviews(String restaurantId, Pageable pageable);
    Optional<Review> getReview(String restaurantId, String reviewId);
    Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest request);
    void  deleteReview(User author, String restaurantId, String reviewId);
}
