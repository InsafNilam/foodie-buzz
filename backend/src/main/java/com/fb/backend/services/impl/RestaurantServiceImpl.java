package com.fb.backend.services.impl;

import com.fb.backend.domain.GeoLocation;
import com.fb.backend.domain.RestaurantCreateUpdateRequest;
import com.fb.backend.domain.entities.Address;
import com.fb.backend.domain.entities.Photo;
import com.fb.backend.domain.entities.Restaurant;
import com.fb.backend.exceptions.RestaurantNotFoundException;
import com.fb.backend.repositories.RestaurantRepository;
import com.fb.backend.services.GeoLocationService;
import com.fb.backend.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;

    @Override
    public Restaurant createRestaurant(RestaurantCreateUpdateRequest request) {
        Address address = request.getAddress();
        GeoLocation geoLocation = geoLocationService.getGeoLocation(request.getAddress());
        GeoPoint geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());

        List<String> photoIds = request.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(photoUrl-> Photo.builder()
                .url(photoUrl)
                .uploadDate(LocalDateTime.now())
                .build())
                .toList();

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .cuisineType(request.getCuisineType())
                .contactInformation(request.getContactInformation())
                .address(address)
                .geoLocation(geoPoint)
                .operatingHours(request.getOperatingHours())
                .averageRating(0f)
                .photos(photos)
                .build();

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Page<Restaurant> searchRestaurant(String query, Float minRating, Float latitude, Float longitude, Float radius, Pageable pageable) {
        if(null != minRating && (null == query || query.isEmpty())) {
            return restaurantRepository.findByAverageRatingGreaterThanEqual(minRating, pageable);
        }

        Float searchMinRating = null == minRating ? 0f: minRating;

        if(null != query && !query.trim().isEmpty()) {
            return restaurantRepository.findByQueryAndMinRating(query, searchMinRating, pageable);
        }

        if(null != latitude && null != longitude && null != radius) {
            return restaurantRepository.findByLocationNear(latitude, longitude, radius, pageable);
        }

        return restaurantRepository.findAll(pageable);
    }

    @Override
    public Optional<Restaurant> getRestaurantById(String id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest request) {
        Restaurant restaurant =  restaurantRepository.findById(id)
                .orElseThrow(()-> new RestaurantNotFoundException("Restaurant with ID doesn't exist: " + id));

        GeoLocation geoLocation = geoLocationService.getGeoLocation(request.getAddress());
        GeoPoint geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());

        List<String> photoIds = request.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(url->Photo.builder()
                .url(url)
                .uploadDate(LocalDateTime.now())
                .build())
                .toList();

        restaurant.setName(request.getName());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setContactInformation(request.getContactInformation());
        restaurant.setAddress(request.getAddress());
        restaurant.setGeoLocation(geoPoint);
        restaurant.setOperatingHours(request.getOperatingHours());
        restaurant.setPhotos(photos);

        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }
}
