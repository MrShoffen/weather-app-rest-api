package org.mrshoffen.weather.http.advice;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.weather.exception.authorization.SessionNotFoundException;
import org.mrshoffen.weather.exception.authorization.SessionExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.mrshoffen.weather.http.advice.ProblemDetailUtil.getProblemDetailResponseEntity;
import static org.mrshoffen.weather.util.CookieUtil.clearCustomCookie;

@RestControllerAdvice
public class SessionControllerAdvice {

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ProblemDetail> handleSessionExpiredException(SessionExpiredException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSessionNotFoundException(SessionNotFoundException e, HttpServletResponse response) {
//        Cookie cookie = clearCustomCookie(sessionCookieName);
//        response.addCookie(cookie);

        return getProblemDetailResponseEntity(HttpStatus.NOT_FOUND, e);
    }
}
