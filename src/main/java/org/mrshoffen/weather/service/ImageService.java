package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Service
@RequiredArgsConstructor
public class ImageService {


    @Value("${app.resources.upload.image-dir}")
    private String imageDir;


    @SneakyThrows
    public void upload(MultipartFile file) {
        String uniqueImageName = UUID.randomUUID() + "_"
                + StringUtils.cleanPath(file.getOriginalFilename());

        Path fullImagePath = getNormalizePath(uniqueImageName);

        try (InputStream content = file.getInputStream()) {
            Files.createDirectories(fullImagePath.getParent());
            Files.write(fullImagePath, content.readAllBytes(), CREATE, TRUNCATE_EXISTING);
        }
    }


    @SneakyThrows
    public byte[] get(String imagePath) {
        Path fullImagePath = getNormalizePath(imagePath);

        return Files.readAllBytes(fullImagePath);
    }

    private Path getNormalizePath(String uniqueImageName) {
        return Path.of(imageDir, uniqueImageName)
                .toAbsolutePath()
                .normalize();
    }

}
