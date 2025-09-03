package com.fb.backend.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ReviewCreateUpdateRequestDTO {
    @NotBlank(message = "Review content is required")
    private String content;

    @NotNull(message = "Review rating is required")
    @Min(value = 1, message = "Review Rating must be between 1 & 5")
    @Max(value = 5, message = "Review Rating must be between 1 & 5")
    private String rating;

    private List<String> photoIds = new ArrayList<>();
}
