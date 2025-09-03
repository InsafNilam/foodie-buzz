package com.fb.backend.manual;

import com.fb.backend.domain.RestaurantCreateUpdateRequest;
import com.fb.backend.domain.entities.Address;
import com.fb.backend.domain.entities.OperatingHours;
import com.fb.backend.domain.entities.Photo;
import com.fb.backend.domain.entities.TimeRange;
import com.fb.backend.services.PhotoService;
import com.fb.backend.services.RestaurantService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Tag("manual")
public class RestaurantDataLoaderTest {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    @Rollback(false) // Allow changes to persist
    public void createSampleRestaurants() throws Exception {
        List<RestaurantCreateUpdateRequest> restaurants = createRestaurantData();
        restaurants.forEach(restaurant -> {
            String fileName = restaurant.getPhotoIds().getFirst();
            Resource resource = resourceLoader.getResource("classpath:data/" + fileName);
            MultipartFile multipartFile = null;

            try{
                multipartFile = new MockMultipartFile(
                        "file",
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        resource.getInputStream()
                );
            }catch (IOException e){
                throw new RuntimeException(e);
            }

            Photo photo = photoService.upload(multipartFile);

            restaurant.setPhotoIds(List.of(photo.getUrl()));

            restaurantService.createRestaurant(restaurant);

            System.out.println("Created restaurant: " + restaurant.getName());
        });
    }

    private List<RestaurantCreateUpdateRequest> createRestaurantData() {
        return Arrays.asList(
                createRestaurant(
                        "The Golden Dragon",
                        "Chinese",
                        "+44 20 7123 4567",
                        createAddress("12", "Gerrard Street", null, "London", "London", "W1D 6JF", "United Kingdom"),
                        createStandardOperatingHours("11:30", "23:00", "11:30", "17:30"),
                        "burger.jpg"
                ),
                createRestaurant(
                        "La Bella Vita",
                        "Italian",
                        "+44 20 7234 9876",
                        createAddress("45", "Wardour Street", null, "London", "London", "W1F 8ZT", "United Kingdom"),
                        createStandardOperatingHours("12:00", "22:30", "12:00", "16:00"),
                        "coffee.jpg"
                ),
                createRestaurant(
                        "El Toro Loco",
                        "Mexican",
                        "+44 20 7456 3210",
                        createAddress("78", "Oxford Street", null, "London", "London", "W1D 1BS", "United Kingdom"),
                        createStandardOperatingHours("11:00", "23:00", "11:00", "17:00"),
                        "lasagna.jpg"
                ),
                createRestaurant(
                        "Bombay Spice",
                        "Indian",
                        "+44 20 7987 1122",
                        createAddress("33", "Brick Lane", null, "London", "London", "E1 6PU", "United Kingdom"),
                        createStandardOperatingHours("12:00", "23:30", "12:00", "18:00"),
                        "meal.jpg"
                ),
                createRestaurant(
                        "Sakura House",
                        "Japanese",
                        "+44 20 7567 8899",
                        createAddress("21", "Piccadilly", null, "London", "London", "W1J 9HL", "United Kingdom"),
                        createStandardOperatingHours("11:30", "22:00", "11:30", "15:30"),
                        "noodles.jpg"
                ),
                createRestaurant(
                        "Le Petit Paris",
                        "French",
                        "+44 20 7455 6677",
                        createAddress("90", "Soho Square", null, "London", "London", "W1D 3QD", "United Kingdom"),
                        createStandardOperatingHours("12:00", "23:00", "12:00", "17:00"),
                        "pasta.jpg"
                ),
                createRestaurant(
                        "Mama’s Diner",
                        "American",
                        "+44 20 7990 2233",
                        createAddress("56", "Baker Street", null, "London", "London", "W1U 7EU", "United Kingdom"),
                        createStandardOperatingHours("10:00", "23:59", "10:00", "18:00"),
                        "pizza.jpg"
                ),
                createRestaurant(
                        "Mediterraneo",
                        "Mediterranean",
                        "+44 20 7666 4455",
                        createAddress("17", "King’s Road", null, "London", "London", "SW3 4RP", "United Kingdom"),
                        createStandardOperatingHours("12:00", "22:00", "12:00", "16:00"),
                        "salad.jpg"
                ),
                createRestaurant(
                        "Seoul Garden",
                        "Korean",
                        "+44 20 7543 9090",
                        createAddress("84", "Chinatown", null, "London", "London", "W1D 5QA", "United Kingdom"),
                        createStandardOperatingHours("11:00", "22:30", "11:00", "15:00"),
                        "sandwich.jpg"
                ),
                createRestaurant(
                        "Casa Flamenca",
                        "Spanish",
                        "+44 20 7333 7788",
                        createAddress("29", "Covent Garden", null, "London", "London", "WC2E 8HD", "United Kingdom"),
                        createStandardOperatingHours("12:00", "23:30", "12:00", "17:30"),
                        "steak.jpg"
                )
        );
    }

    private RestaurantCreateUpdateRequest createRestaurant(String name, String cuisineType, String contactInformation, Address address, OperatingHours operatingHours, String photoId){
        return RestaurantCreateUpdateRequest.builder()
                .name(name)
                .cuisineType(cuisineType)
                .contactInformation(contactInformation)
                .address(address)
                .operatingHours(operatingHours)
                .photoIds(List.of(photoId))
                .build();
    }

    private Address createAddress(String streetNumber, String streetName, String unit, String city, String state, String postalCode, String country){
        return Address.builder()
                .streetNumber(streetNumber)
                .streetName(streetName)
                .unit(unit)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country(country)
                .build();
    }

    private OperatingHours createStandardOperatingHours(String weekdayOpen, String weekdayClose, String weekendOpen, String weekendClose){
        TimeRange weekday = TimeRange.builder()
                .openTime(weekdayOpen)
                .closeTime(weekdayClose)
                .build();

        TimeRange weekend = TimeRange.builder()
                .openTime(weekendOpen)
                .closeTime(weekendClose)
                .build();

        return OperatingHours.builder()
                .monday(weekday)
                .tuesday(weekday)
                .wednesday(weekday)
                .thursday(weekday)
                .friday(weekday)
                .saturday(weekend)
                .sunday(weekend)
                .build();
    }
}
