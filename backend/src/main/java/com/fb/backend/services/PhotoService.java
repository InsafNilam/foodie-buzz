package com.fb.backend.services;

import com.fb.backend.domain.entities.Photo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PhotoService {
    Photo upload(MultipartFile file);
    Optional<Resource> load(String id);
}
