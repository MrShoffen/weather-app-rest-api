package org.mrshoffen.weather.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.dto.UserLoginDto;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.dto.UserResponseDto;
import org.mrshoffen.weather.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.mrshoffen.weather.util.CookieUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Value("${session.cookie.max-age}")
    private int sessionCookieAge;

    @Value("${session.cookie.name}")
    private String sessionCookieName;

    @PostMapping("/registration")
    ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto userRegistrationDto) throws URISyntaxException {
        UserResponseDto register = authenticationService.register(userRegistrationDto);

        return ResponseEntity
                .created(new URI("/weather/api/v1/users/" + register.getId()))
                .body(register);
    }

    @PostMapping("/login")
    ResponseEntity<Void> login(@RequestBody UserLoginDto userLoginDto,
                                          HttpServletResponse response) throws URISyntaxException {

        UUID uuid = authenticationService.login(userLoginDto);

        Cookie cookie = createCustomCookie(sessionCookieName, uuid.toString(), sessionCookieAge);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

}
