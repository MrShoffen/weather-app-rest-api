package org.mrshoffen.weather.model.dto.out;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDto {
    private UUID id;
    private UserResponseDto user;
}
