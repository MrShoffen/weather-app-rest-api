package org.mrshoffen.weather.model.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditProfileDto {
    private String newUsername;
    private String newAvatarUrl;
}
