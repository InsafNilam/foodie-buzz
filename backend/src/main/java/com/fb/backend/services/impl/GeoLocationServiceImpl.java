package com.fb.backend.services.impl;

import com.fb.backend.domain.GeoLocation;
import com.fb.backend.domain.entities.Address;
import com.fb.backend.services.GeoLocationService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GeoLocationServiceImpl implements GeoLocationService {
    private static final float MIN_LATITUDE = 51.28f;
    private static final float MAX_LATITUDE = 51.686f;
    private static final float MIN_LONGITUDE = -0.489f;
    private static final float MAX_LONGITUDE = 0.236f;

    @Override
    public GeoLocation getGeoLocation(Address address) {
        Random random = new Random();
        double latitude = MIN_LATITUDE + random.nextDouble() * (MAX_LATITUDE-MIN_LATITUDE);
        double longitude = MIN_LONGITUDE + random.nextDouble() * (MAX_LONGITUDE-MIN_LONGITUDE);

        return GeoLocation.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
