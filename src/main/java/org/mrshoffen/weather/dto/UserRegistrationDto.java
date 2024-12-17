package org.mrshoffen.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    private String username;
    private String password;
    private MultipartFile avatar;
}
