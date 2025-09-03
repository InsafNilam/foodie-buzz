package com.fb.backend.controllers;

import com.fb.backend.domain.ReviewCreateUpdateRequest;
import com.fb.backend.domain.dtos.ReviewCreateUpdateRequestDTO;
import com.fb.backend.domain.dtos.ReviewDTO;
import com.fb.backend.domain.entities.Review;
import com.fb.backend.domain.entities.User;
import com.fb.backend.mappers.ReviewMapper;
import com.fb.backend.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants/{id}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@PathVariable String id, @Valid @RequestBody ReviewCreateUpdateRequestDTO dto, @AuthenticationPrincipal Jwt jwt) {
        ReviewCreateUpdateRequest request = reviewMapper.reviewToReviewCreateUpdateRequest(dto);
        User author = jwtToUser(jwt);

        Review review = reviewService.createReview(author, id, request);
        ReviewDTO reviewDTO = reviewMapper.reviewToReviewDTO(review);

        return ResponseEntity.ok(reviewDTO);

    }

    @GetMapping
    public Page<ReviewDTO> getReviews(@PathVariable String id, @PageableDefault(size = 20, page = 0, sort = "datePosted", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Review> reviews = reviewService.listReviews(id, pageable);
        return reviews.map(reviewMapper::reviewToReviewDTO);
    }

    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable String id, @PathVariable String reviewId) {
        return reviewService.getReview(id, reviewId)
                .map(reviewMapper::reviewToReviewDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable String id, @PathVariable String reviewId, @Valid @RequestBody ReviewCreateUpdateRequestDTO dto, @AuthenticationPrincipal Jwt jwt) {
        ReviewCreateUpdateRequest request = reviewMapper.reviewToReviewCreateUpdateRequest(dto);
        User author = jwtToUser(jwt);

        Review review = reviewService.updateReview(author, id, reviewId, request);
        ReviewDTO reviewDTO = reviewMapper.reviewToReviewDTO(review);

        return ResponseEntity.ok(reviewDTO);
    }

    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id, @PathVariable String reviewId, @AuthenticationPrincipal Jwt jwt) {
        User author = jwtToUser(jwt);
        reviewService.deleteReview(author, id, reviewId);

        return ResponseEntity.noContent().build();
    }

    private User jwtToUser(Jwt jwt) {
        return User.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .givenName(jwt.getClaimAsString("given_name"))
                .familyName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
