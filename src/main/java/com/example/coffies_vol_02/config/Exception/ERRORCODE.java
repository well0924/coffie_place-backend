package com.example.coffies_vol_02.config.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ERRORCODE{
    INVALID_PARAMETER(400,"유효하지 않은 파라미터입니다."),
    NOT_MEMBER(404,"회원이 없습니다."),
    NOT_FOUND_MEMBER(404,"회원이 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(500,"서버 에러입니다.");
    private final Integer httpStatus;
    private final String message;
}
