package com.fb.backend.services;

import com.fb.backend.domain.GeoLocation;
import com.fb.backend.domain.entities.Address;

public interface GeoLocationService {
    GeoLocation getGeoLocation(Address address);
}
