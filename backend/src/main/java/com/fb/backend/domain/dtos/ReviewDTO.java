package com.fb.backend.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    private String id;

    private String content;

    private Integer rating;

    private LocalDateTime datePosted;

    private LocalDateTime lastEdited;

    private List<PhotoDTO> photos = new ArrayList<>();

    private UserDTO writtenBy;
}
