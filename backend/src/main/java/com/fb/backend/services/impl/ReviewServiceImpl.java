package com.fb.backend.services.impl;

import com.fb.backend.domain.ReviewCreateUpdateRequest;
import com.fb.backend.domain.entities.Photo;
import com.fb.backend.domain.entities.Restaurant;
import com.fb.backend.domain.entities.Review;
import com.fb.backend.domain.entities.User;
import com.fb.backend.exceptions.RestaurantNotFoundException;
import com.fb.backend.exceptions.ReviewNotAllowedException;
import com.fb.backend.repositories.RestaurantRepository;
import com.fb.backend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final RestaurantRepository restaurantRepository;

    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID not found: " + restaurantId));

        boolean hasExistingReview = restaurant.getReviews().stream()
                .anyMatch(r->r.getWrittenBy().getId().equals(author.getId()));

        if (hasExistingReview) {
            throw new ReviewNotAllowedException("User has already reviewed this restaurant");
        }

        List<String> photoIds = request.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(url -> Photo.builder()
                        .url(url)
                        .uploadDate(LocalDateTime.now())
                        .build())
                .toList();

        String reviewId = UUID.randomUUID().toString();
        Review review = Review.builder()
                .id(reviewId)
                .content(request.getContent())
                .rating(request.getRating())
                .photos(photos)
                .datePosted(LocalDateTime.now())
                .lastEdited(LocalDateTime.now())
                .writtenBy(author)
                .build();

        restaurant.getReviews().add(review);
        updateRestaurantAverageRating(restaurant);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return savedRestaurant.getReviews()
                .stream()
                .filter(r->reviewId.equals(r.getId()))
                .findFirst()
                .orElseThrow(()->new RuntimeException("Error retrieving created review"));
    }

    @Override
    public Page<Review> listReviews(String restaurantId, Pageable pageable) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID not found: " + restaurantId));

        List<Review> reviews = restaurant.getReviews();

        Sort sort = pageable.getSort();
        if(sort.isSorted()) {
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            boolean isAscending = order.getDirection().isAscending();

            Comparator<Review> comparator = switch (property) {
                case "datePosted" -> Comparator.comparing(Review::getDatePosted);
                case "rating" -> Comparator.comparing(Review::getRating);
                default -> Comparator.comparing(Review::getDatePosted);
            };

            reviews.sort(isAscending ? comparator : comparator.reversed());
        }else{
            reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());
        }

        int start = (int) pageable.getOffset();
        if(start >= reviews.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, reviews.size());
        }

        int end = Math.min(start + pageable.getPageSize(), reviews.size());
        return new PageImpl<>(reviews.subList(start, end), pageable, reviews.size());
    }

    @Override
    public Optional<Review> getReview(String restaurantId, String reviewId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID not found: " + restaurantId));

        return restaurant.getReviews()
                .stream()
                .filter(r->reviewId.equals(r.getId()))
                .findFirst();
    }

    @Override
    public Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID not found: " + restaurantId));

        String authorId = author.getId();
        Review review = restaurant.getReviews().stream().filter(r->reviewId.equals(r.getId())).findFirst().orElseThrow(()-> new ReviewNotAllowedException("Review doesn't exists"));

        if(!authorId.equals(review.getWrittenBy().getId())){
            throw new  ReviewNotAllowedException("Cannot update another user review");
        }

        if(LocalDateTime.now().isAfter(review.getDatePosted().plusHours(48))){
            throw new ReviewNotAllowedException("Review can no longer be edited");
        }

        List<String> photoIds = request.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(url -> Photo.builder()
                        .url(url)
                        .uploadDate(LocalDateTime.now())
                        .build())
                .toList();

        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setLastEdited(LocalDateTime.now());
        review.setPhotos(photos);

        List<Review> updatedReviews = restaurant.getReviews().stream().filter(r->!reviewId.equals(r.getId())).collect(Collectors.toList());
        updatedReviews.add(review);

        restaurant.setReviews(updatedReviews);
        updateRestaurantAverageRating(restaurant);

        restaurantRepository.save(restaurant);

        return review;
    }

    @Override
    public void deleteReview(User author, String restaurantId, String reviewId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID not found: " + restaurantId));

        String authorId = author.getId();
        Review review = restaurant.getReviews().stream().filter(r->reviewId.equals(r.getId())).findFirst().orElseThrow(()-> new ReviewNotAllowedException("Review doesn't exists"));

        if(!authorId.equals(review.getWrittenBy().getId())){
            throw new  ReviewNotAllowedException("Cannot delete another user review");
        }

        List<Review> filteredReviews = restaurant.getReviews()
                .stream()
                .filter(r -> !reviewId.equals(r.getId()))
                .toList();

        restaurant.setReviews(filteredReviews);
        updateRestaurantAverageRating(restaurant);

        restaurantRepository.save(restaurant);
    }

    private void updateRestaurantAverageRating(Restaurant restaurant){
        List<Review> reviews = restaurant.getReviews();
        if(reviews.isEmpty()){
            restaurant.setAverageRating(0.0f);
        }else{
            double averageRating = reviews.stream().mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0f);

            restaurant.setAverageRating((float) averageRating);
        }
    }
}
