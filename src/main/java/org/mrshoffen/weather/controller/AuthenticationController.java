package org.mrshoffen.weather.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.dto.UserLoginDto;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.dto.UserResponseDto;
import org.mrshoffen.weather.exception.authorization.UserUnauthorizedException;
import org.mrshoffen.weather.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.mrshoffen.weather.util.CookieUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Value("${session.minutes-before-expire}")
    private int sessionCookieAge;

    @Value("${session.cookie-name}")
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
                               HttpServletResponse response) {

        UUID uuid = authenticationService.login(userLoginDto);

        Cookie cookie = createCustomCookie(sessionCookieName, uuid.toString(), sessionCookieAge * 60);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@CookieValue("${session.cookie-name}") UUID sessionId,
                                HttpServletResponse response) {

        authenticationService.logout(sessionId);
        Cookie cookie = clearCustomCookie(sessionCookieName);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
   }


    @GetMapping("/test")
    ResponseEntity<UserResponseDto> test() {
        return ResponseEntity.ok(new UserResponseDto(22, "test"));
    }

}
