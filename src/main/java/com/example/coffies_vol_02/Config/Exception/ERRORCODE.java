package com.example.coffies_vol_02.Config.Exception;

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
    NOT_REPLY(404,"댓글이 없습니다."),
    LIKE_NOT_FOUND(404,"좋아요가 없습니다."),
    NOT_MATCH_PASSWORD(400,"비밀번호가 일치하지 않습니다."),
    NOT_FILE(400,"파일이 존재하지 않습니다."),
    PLACE_NOT_FOUND(400,"가게가 존재하지 않습니다."),
    NOT_WISHLIST(400,"위시리스트가 없습니다.");
    private final Integer httpStatus;
    private final String message;
}
