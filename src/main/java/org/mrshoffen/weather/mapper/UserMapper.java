package org.mrshoffen.weather.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {



    @Mapping(target = "id", ignore = true)
    User toEntity(UserRegistrationDto registrationDto);

    @Named("userToDtoMethod")
    UserResponseDto toResponseDto(User user);
}
