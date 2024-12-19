package org.mrshoffen.weather.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.SessionResponseDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.model.entity.UserSession;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface SessionMapper {


    @Mapping(target = "user", source = "user", qualifiedByName = "userToDtoMethod")
    SessionResponseDto toResponseDto(UserSession session);
}
