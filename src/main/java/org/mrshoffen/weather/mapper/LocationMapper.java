package org.mrshoffen.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mrshoffen.weather.model.dto.in.LocationSaveDto;
import org.mrshoffen.weather.model.dto.out.LocationDto;
import org.mrshoffen.weather.model.entity.Location;
import org.mrshoffen.weather.util.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Location toEntity(LocationSaveDto locationSaveDto, Integer userId);

    LocationDto toDto(Location location);
}
