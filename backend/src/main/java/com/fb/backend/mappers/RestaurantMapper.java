package com.fb.backend.mappers;

import com.fb.backend.domain.RestaurantCreateUpdateRequest;
import com.fb.backend.domain.dtos.GeoPointDTO;
import com.fb.backend.domain.dtos.RestaurantCreateUpdateRequestDTO;
import com.fb.backend.domain.dtos.RestaurantDTO;
import com.fb.backend.domain.dtos.RestaurantSummaryDTO;
import com.fb.backend.domain.entities.Restaurant;
import com.fb.backend.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {
    RestaurantCreateUpdateRequest toRestaurantCreateUpdateRequest(RestaurantCreateUpdateRequestDTO dto);

    @Mapping(source="reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantDTO toRestaurantDTO(Restaurant restaurant);

    @Mapping(source="reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantSummaryDTO  toRestaurantSummaryDTO(Restaurant restaurant);

    @Named("populateTotalReviews")
    default Integer populateTotalReviews(List<Review> reviews){
        return reviews.size();
    }

    @Mapping(target = "latitude", expression = "java(geoPoint.getLat())")
    @Mapping(target = "longitude", expression = "java(geoPoint.getLon())")
    GeoPointDTO toGeoPointDTO(GeoPoint geoPoint);
}
