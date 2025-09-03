package com.fb.backend.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface StorageService {
    String upload(MultipartFile file, String filename);
    Optional<Resource> load(String id);
}
