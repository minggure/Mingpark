package com.example.mingpark.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 상세 페이지에서 존재하지 않는 공연을 요청했을 때 404 응답을 반환한다.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConcertNotFoundException extends RuntimeException {

    public ConcertNotFoundException(Long concertId) {
        super("존재하지 않는 공연입니다. concertId=" + concertId);
    }
}
