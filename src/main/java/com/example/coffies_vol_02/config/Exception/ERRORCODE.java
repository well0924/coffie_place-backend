package com.example.coffies_vol_02.config.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ERRORCODE{
    INVALID_PARAMETER(400,"유효하지 않은 파라미터입니다."),
    NOT_MEMBER(404,"회원이 없습니다."),
    NOT_FOUND_MEMBER(404,"회원이 존재하지 않습니다."),
    ONLY_USER(400,"회원만 가능합니다."),
    INTERNAL_SERVER_ERROR(500,"서버 에러입니다."),
    BOARD_NOT_FOUND(404,"게시글이 없습니다."),
    BOARD_NOT_LIST(400,"게시글이 없습니다."),
    NOT_AUTH(401,"권한이 없습니다."),
    NOT_REPLY(404,"댓글이 없습니다.");
    private final Integer httpStatus;
    private final String message;
}
