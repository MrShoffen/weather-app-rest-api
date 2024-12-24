package org.mrshoffen.weather.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mrshoffen.weather.exception.image.ImageNotFoundException;
import org.mrshoffen.weather.exception.image.IncorrectImageFormatException;
import org.mrshoffen.weather.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension.class)
class ImageControllerIT {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;


    private final static String BASE_URL = "/weather/api/images";

    @Test
    void uploadImage_ReturnsCreatedWithImageUrl() throws Exception {
        //given
        String uploadedFileName = "image.jpg";
        when(imageService.upload(any(MultipartFile.class)))
                .thenReturn(uploadedFileName);

        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "mock-image-data".getBytes()
        );

        //when
        mockMvc.perform(multipart(BASE_URL)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"imageUrl":"/weather/api/images/image.jpg"}
                                """)
                );

        verify(imageService).upload(any(MultipartFile.class));
    }


    @Test
    void getImage_ReturnsImageBytes() throws Exception {
        //given
        byte[] mockImageBytes = "image-bytes".getBytes();
        when(imageService.get(anyString()))
                .thenReturn(mockImageBytes);

        //when
        mockMvc.perform(get(BASE_URL + "/image.jpg"))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.IMAGE_JPEG),
                        content().bytes(mockImageBytes)
                );

        verify(imageService).get("image.jpg");
    }

    @Test
    void getImage_FileNotFound_ReturnsProblemDetail() throws Exception {
        //given
        Mockito.when(imageService.get(Mockito.anyString()))
                .thenThrow(new ImageNotFoundException("Image with name 'not_correct_image.png' not found!"));

        //when
        mockMvc.perform(get(BASE_URL + "/not_correct_image.png"))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"ImageNotFoundException","status":404,"detail":"Image with name 'not_correct_image.png' not found!"}
                                """)
                );

        Mockito.verify(imageService).get("not_correct_image.png");
    }

    @Test
    void uploadImage_IncorrectFormat_ReturnsProblemDetail() throws Exception {
        //given
        when(imageService.upload(any(MultipartFile.class)))
                .thenThrow(new IncorrectImageFormatException("Unsupported image format: image.gif" ));

        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "image.gif",
                MediaType.IMAGE_JPEG_VALUE,
                "mock-image-data".getBytes()
        );

        //when
        mockMvc.perform(multipart(BASE_URL)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"type":"about:blank","title":"IncorrectImageFormatException","status":400,"detail":"Unsupported image format: image.gif"}
                                """)
                );

        verify(imageService).upload(any(MultipartFile.class));
    }

}
