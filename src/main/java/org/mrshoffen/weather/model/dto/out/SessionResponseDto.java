package org.mrshoffen.weather.model.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDto {
    private UUID id;
    private UserResponseDto user;
}
