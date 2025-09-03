package com.fb.backend.domain.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantCreateUpdateRequestDTO {
    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Cuisine Type is required")
    private String cuisineType;

    @NotBlank(message = "Contact Information is required")
    private String contactInformation;

    @Valid
    private AddressDTO address;

    @Valid
    private OperatingHoursDTO operatingHours;

    @Size(min = 1, message = "At least one photo ID is required")
    private List<String> photoIds;
}
