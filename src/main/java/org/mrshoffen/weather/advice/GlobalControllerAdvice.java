package org.mrshoffen.weather.advice;


import org.mrshoffen.weather.exception.EntityAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleEntityAlreadyExistException(EntityAlreadyExistException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }


}
