package com.fb.backend.services;

import com.fb.backend.domain.RestaurantCreateUpdateRequest;
import com.fb.backend.domain.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantService {
    Restaurant createRestaurant(RestaurantCreateUpdateRequest request);
    Page<Restaurant> searchRestaurant(String query, Float minRating, Float latitude, Float longitude, Float radius, Pageable pageable);
    Optional<Restaurant> getRestaurantById(String id);
    Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest request);
    void deleteRestaurant(String id);
}
