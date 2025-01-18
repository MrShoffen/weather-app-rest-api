package org.mrshoffen.weather.http.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.in.UserLoginDto;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.SessionResponseDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.mrshoffen.weather.util.CookieUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Value("${app.session.cookie.max-age-hours}")
    private int sessionCookieAge;

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    @PostMapping(value = "/registration")
    ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) throws URISyntaxException {
        UserResponseDto register = authenticationService.register(userRegistrationDto);


        return ResponseEntity
                .created(new URI("/weather/api/user"))
                .body(register);
    }

    @PostMapping("/login")
    ResponseEntity<UserResponseDto> login(@Valid @RequestBody UserLoginDto userLoginDto,
                                          HttpServletResponse response) {

        SessionResponseDto session = authenticationService.login(userLoginDto);
        Cookie cookie = createCustomCookie(sessionCookieName,
                session.getId().toString(),
                sessionCookieAge * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(session.getUser());
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@CookieValue("${app.session.cookie.name}") UUID sessionId,
                                HttpServletResponse response) {

        authenticationService.logout(sessionId);
        Cookie cookie = clearCustomCookie(sessionCookieName);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }


}
