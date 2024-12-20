package org.mrshoffen.weather.model.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditDto {
    private String username;
    private String avatarUrl;
    private String oldPassword;
    private String newPassword;
}
