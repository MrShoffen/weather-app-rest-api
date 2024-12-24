package org.mrshoffen.weather.http.controller;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/images")
public class ImageController {

    @Value("${app.upload.image.base-url}")
    private String baseImagePath;

    private final ImageService imageService;

    @PostMapping(consumes = {"multipart/form-data"})
    ResponseEntity<Map<String, String>> uploadImage(@RequestParam("avatar") MultipartFile avatar) {
        String imageName = imageService.upload(avatar);
        URI location = URI.create(baseImagePath + imageName);

        Map<String, String> map = new HashMap<>();
        map.put("imageUrl", location.toString());
        return ResponseEntity
                .created(location)
                .body(map);
    }

    @GetMapping(value = "/{filename}")
    ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        byte[] bytes = imageService.get(filename);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

}
