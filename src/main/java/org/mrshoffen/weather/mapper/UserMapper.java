package org.mrshoffen.weather.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.util.PasswordEncoder;

@Mapper(componentModel = "spring", uses = PasswordEncoder.class)
public interface UserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    User toEntity(UserRegistrationDto registrationDto);

    @Named("userToDtoMethod")
    UserResponseDto toResponseDto(User user);
}
