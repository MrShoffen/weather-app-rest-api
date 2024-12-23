package org.mrshoffen.weather.http.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mrshoffen.weather.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    private String baseImagePath = "/weather/images/";

    @Mock
    ImageService imageService;

    @InjectMocks
    ImageController imageController;

    @BeforeEach
    void setUp() {
        setField(imageController, "baseImagePath", baseImagePath);
    }


    @Test
    void uploadImage() {
        //given
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test-image.png", "test-image.png", "image/png", "image/png".getBytes());

        String uniqueImageName = UUID.randomUUID() + ".png";
        doReturn(uniqueImageName)
                .when(imageService)
                .upload(mockMultipartFile);

        //when
        ResponseEntity<Map<String, String>> response = imageController.uploadImage(mockMultipartFile);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("imageUrl")).isEqualTo(baseImagePath + uniqueImageName);

        verify(imageService).upload(mockMultipartFile);
        verifyNoMoreInteractions(imageService);
    }

    @Test
    void getImage() {
        //given
        String uniqueImageName = UUID.randomUUID().toString() + ".png";
        byte[] mockBytes = "image/png".getBytes();

        doReturn(mockBytes)
                .when(imageService)
                .get(uniqueImageName);

        //when
        ResponseEntity<byte[]> response = imageController.getImage(uniqueImageName);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockBytes);

        verify(imageService).get(uniqueImageName);
        verifyNoMoreInteractions(imageService);
    }
}