package org.mrshoffen.weather.controller;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.dto.UserResponseDto;
import org.mrshoffen.weather.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/registration")
    ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto userRegistrationDto) throws URISyntaxException {
        UserResponseDto register = userService.register(userRegistrationDto);

        return ResponseEntity
                .created(new URI("/weather/api/v1/users/" + register.getId()))
                .body(register);
    }

}
