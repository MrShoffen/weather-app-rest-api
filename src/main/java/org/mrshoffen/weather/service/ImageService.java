package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mrshoffen.weather.exception.image.ImageNotFoundException;
import org.mrshoffen.weather.exception.image.IncorrectImageFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Service
@RequiredArgsConstructor
public class ImageService {


    @Value("${app.upload.image.directory}")
    private String imageDir;

    @Value("${app.upload.image.allowed-formats}")
    private List<String> allowedFormats;


    @SneakyThrows
    public String upload(MultipartFile file) {
        String uniqueImageName = generateUniqueFileName(file.getOriginalFilename());
        Path fullImagePath = getNormalizeAbsolutePath(uniqueImageName);

        try (InputStream content = file.getInputStream()) {
            Files.createDirectories(fullImagePath.getParent());
            Files.write(fullImagePath, content.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }

        return uniqueImageName;
    }

    @SneakyThrows
    public byte[] get(String imagePath) {
        Path fullImagePath = getNormalizeAbsolutePath(imagePath);

        if (!Files.exists(fullImagePath)) {
            throw new ImageNotFoundException("Image with name '" + imagePath + "' not found!");
        }
        return Files.readAllBytes(fullImagePath);
    }

    private String generateUniqueFileName(String originalFilename) {
        return allowedFormats.stream()
                .filter(originalFilename::endsWith)
                .findFirst()
                .map(s -> UUID.randomUUID() + s)
                .orElseThrow(() -> new IncorrectImageFormatException("Unsupported image format: " + originalFilename));
    }


    private Path getNormalizeAbsolutePath(String uniqueImageName) {
        return Path.of(imageDir, uniqueImageName)
                .toAbsolutePath()
                .normalize();
    }

}
