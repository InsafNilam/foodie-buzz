package com.fb.backend.controllers;

import com.fb.backend.domain.RestaurantCreateUpdateRequest;
import com.fb.backend.domain.dtos.RestaurantCreateUpdateRequestDTO;
import com.fb.backend.domain.dtos.RestaurantDTO;
import com.fb.backend.domain.dtos.RestaurantSummaryDTO;
import com.fb.backend.domain.entities.Restaurant;
import com.fb.backend.mappers.RestaurantMapper;
import com.fb.backend.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateRequestDTO request) {
        RestaurantCreateUpdateRequest restaurantCreateUpdateRequest = restaurantMapper.toRestaurantCreateUpdateRequest(request);
        Restaurant restaurant = restaurantService.createRestaurant(restaurantCreateUpdateRequest);

        RestaurantDTO restaurantDTO = restaurantMapper.toRestaurantDTO(restaurant);
        return ResponseEntity.ok(restaurantDTO);
    }

    @GetMapping
    public Page<RestaurantSummaryDTO> searchRestaurants(@RequestParam(required = false) String q, @RequestParam(required = false) Float minRating, @RequestParam(required = false) Float latitude, @RequestParam(required = false) Float longitude, @RequestParam(required = false) Float radius, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        Page<Restaurant> searchResults = restaurantService.searchRestaurant(q, minRating, latitude, longitude, radius, PageRequest.of(page-1, size));

        return searchResults.map(restaurantMapper::toRestaurantSummaryDTO);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable("id") String id) {
        return restaurantService.getRestaurantById(id)
                .map(restaurant -> ResponseEntity.ok(restaurantMapper.toRestaurantDTO(restaurant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable("id") String id, @Valid @RequestBody RestaurantCreateUpdateRequestDTO requestDTO){
        RestaurantCreateUpdateRequest request = restaurantMapper.toRestaurantCreateUpdateRequest(requestDTO);
        Restaurant restaurant = restaurantService.updateRestaurant(id, request);

        RestaurantDTO restaurantDTO = restaurantMapper.toRestaurantDTO(restaurant);
        return ResponseEntity.ok(restaurantDTO);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
