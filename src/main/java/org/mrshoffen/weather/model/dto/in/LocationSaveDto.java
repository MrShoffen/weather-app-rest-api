package org.mrshoffen.weather.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Currency;

@Data
public class LocationSaveDto {
    //todo add validation

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
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;
}
