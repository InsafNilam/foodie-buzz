package com.fb.backend.mappers;

import com.fb.backend.domain.ReviewCreateUpdateRequest;
import com.fb.backend.domain.dtos.ReviewCreateUpdateRequestDTO;
import com.fb.backend.domain.dtos.ReviewDTO;
import com.fb.backend.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    ReviewCreateUpdateRequest reviewToReviewCreateUpdateRequest(ReviewCreateUpdateRequestDTO dto);
    ReviewDTO reviewToReviewDTO(Review review);
}
