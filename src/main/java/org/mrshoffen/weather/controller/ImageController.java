package org.mrshoffen.weather.controller;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mrshoffen.weather.dto.UserResponseDto;
import org.mrshoffen.weather.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;


    @PostMapping(consumes = {"multipart/form-data"})
    ResponseEntity<String> uploadImage(@RequestParam("avatar") MultipartFile avatar) {

        imageService.upload(avatar);

        return null;
    }


    @GetMapping(value = "/{filename}")
    ResponseEntity< byte[]> getImage(@PathVariable String filename) {


        byte[] bytes = imageService.get(filename);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

}
