package com.fb.backend.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDTO {
    private String id;

    private String name;

    private String cuisineType;

    private String contactInformation;

    private Float averageRating;

    private GeoPointDTO geoLocation;

    private AddressDTO address;

    private OperatingHoursDTO operatingHours;

    private List<PhotoDTO> photos = new ArrayList<>();

    private List<ReviewDTO> reviews = new ArrayList<>();

    private UserDTO createdBy;

    private Integer totalReviews;
}
