package org.mrshoffen.weather.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationSaveDto {
    //todo add validation
    private String name;

    private String state;

    private String country;

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;
}
