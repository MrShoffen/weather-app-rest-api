package org.mrshoffen.weather.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Currency;
import org.mapstruct.Mapping;
import org.springframework.format.annotation.NumberFormat;

@Data
public class LocationSaveDto {

    @Size(max = 128, message = "Name can't be larger than 128 symbols")
    @NotNull(message = "Name can't be null!")
    @NotBlank(message = "Name can't be empty!")
    private String name;

    @Size(max = 128, message = "Name can't be larger than 128 symbols")
    private String state;

    @NotNull(message = "Country can't be null!")
    @Size(min = 2, max = 2, message = "Country must be exactly 2 characters long.")
    private String country;

    @JsonProperty("lat")
    @NotNull(message = "Latitude can't be null!")
    @Min(value = -90, message = "Latitude must be a number between -90 and 90.")
    @Max(value = 90, message = "Latitude must be a number between -90 and 90.")
    private Double latitude;

    @JsonProperty("lon")
    @NotNull(message = "Longitude can't be null!")
    @Min(value = -180, message = "Longitude must be a number between -180 and 180.")
    @Max(value = 180, message = "Longitude must be a number between -180 and 180.")
    private Double longitude;
}
