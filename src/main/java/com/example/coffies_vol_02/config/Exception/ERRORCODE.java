package com.example.coffies_vol_02.config.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ERRORCODE implements ErrorDto{
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST,"유효하지 않은 파라미터입니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND,"회원이 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 에러입니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
