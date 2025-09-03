package com.fb.backend.services.impl;

import com.fb.backend.domain.entities.Photo;
import com.fb.backend.services.PhotoService;
import com.fb.backend.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final StorageService storageService;

    @Override
    public Photo upload(MultipartFile file) {
        String photoId = UUID.randomUUID().toString();
        String url = storageService.upload(file, photoId);

        return Photo.builder()
                .url(url)
                .uploadDate(LocalDateTime.now())
                .build();
    }

    @Override
    public Optional<Resource> load(String id) {
        return storageService.load(id);
    }
}
