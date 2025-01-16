package org.mrshoffen.weather.model.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @Size(min = 5, max = 20, message = "Incorrect name length! Must be between  {min} and {max}")
    @NotBlank(message = "Name can't be null!")
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z_]*[a-zA-Z.]+$", message = "Incorrect symbols in username!")
    private String username;

    @Size(min = 5, max = 20, message = "Incorrect password length! Must be between  {min} and {max}")
    @NotNull(message = "Password can't be null!")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*(),.?:{}|<>/`~+=-_';]*$", message = "Incorrect symbols in password!")
    private String password;

    private String avatarUrl;
}
