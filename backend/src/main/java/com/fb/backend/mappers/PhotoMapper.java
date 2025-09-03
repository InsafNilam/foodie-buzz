package com.fb.backend.mappers;

import com.fb.backend.domain.dtos.PhotoDTO;
import com.fb.backend.domain.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {
    PhotoDTO toDto(Photo photo);
    Photo toEntity(PhotoDTO photoDTO);
}
