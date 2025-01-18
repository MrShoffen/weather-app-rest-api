package org.mrshoffen.weather.http.advice;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ProblemDetailUtil {

    public static ResponseEntity<ProblemDetail> getProblemDetailResponseEntity(HttpStatus status, Exception e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setTitle(e.getClass().getSimpleName());
        return ResponseEntity
                .status(status)
                .body(problemDetail);
    }
}
