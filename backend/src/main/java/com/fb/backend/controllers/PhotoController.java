package com.fb.backend.controllers;

import com.fb.backend.domain.dtos.PhotoDTO;
import com.fb.backend.domain.entities.Photo;
import com.fb.backend.mappers.PhotoMapper;
import com.fb.backend.services.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/photos")
public class PhotoController {
    private final PhotoService photoService;
    private final PhotoMapper photoMapper;

    @PostMapping
    public PhotoDTO uploadPhoto(@RequestParam("file") MultipartFile file) {
        Photo photo = photoService.upload(file);
        return photoMapper.toDto(photo);
    }

    @GetMapping(path = "/{id:.+}")
    public ResponseEntity<Resource> getPhotoById(@PathVariable String id) {
       return photoService.load(id).map(photo->
                ResponseEntity.ok()
                        .contentType(MediaTypeFactory
                                .getMediaType(photo)
                                .orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                        .body(photo)
       ).orElse(ResponseEntity.notFound().build());
    }
}
