package org.mrshoffen.weather.http.advice;

import org.mrshoffen.weather.exception.weather.OpenWeatherApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.mrshoffen.weather.http.advice.ProblemDetailUtil.getProblemDetailResponseEntity;

@RestControllerAdvice
public class OpenWeatherApiControllerAdvice {

    @ExceptionHandler(OpenWeatherApiException.class)
    public ResponseEntity<ProblemDetail> handleOpenWeatherApiException(OpenWeatherApiException e) {
        return getProblemDetailResponseEntity(HttpStatus.SERVICE_UNAVAILABLE, e);
    }
}
