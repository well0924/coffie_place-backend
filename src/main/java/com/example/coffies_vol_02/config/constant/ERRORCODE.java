package com.example.coffies_vol_02.config.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@ApiModel(value = "에러 응답",description = "에러 응답 ENUM")
@AllArgsConstructor
public enum ERRORCODE{
    INVALID_PARAMETER(400,"유효하지 않은 파라미터입니다."),
    INTERNAL_SERVER_ERROR(500,"서버 에러입니다."),
    PASSWORD_NOT_MATCH(400,"비밀번호가 일치하지 않습니다."),
    NOT_SEARCH_VALUE(400,"검색결과가 없습니다."),
    NOT_MEMBER_LIST(404,"회원목록이 없습니다."),
    NOT_FOUND_MEMBER(404,"회원이 존재하지 않습니다."),
    ONLY_USER(500,"회원만 가능합니다."),
    BOARD_NOT_FOUND(404,"게시글이 없습니다."),
    BOARD_FAIL(500,"게시글 작성에 실패했습니다"),
    NOTICE_FAIL(500,"공지 게시글 작성에 실패했습니다."),
    NOT_AUTH(401,"권한이 없습니다."),
    NOT_REPLY(404,"댓글이 없습니다."),
    REPLY_FAIL(500,"댓글작성에 실패했습니다."),
    LIKE_FAIL(500,"좋아요 기능이 작동되지 않습니다"),
    LIKE_NOT_FOUND(404,"좋아요가 없습니다."),
    NOT_MATCH_PASSWORD(400,"비밀번호가 일치하지 않습니다."),
    NOT_FILE(400,"파일이 존재하지 않습니다."),
    PLACE_FAIL(500,"가게등록에 실패했습니다"),
    PLACE_NOT_FOUND(400,"가게가 존재하지 않습니다."),
    SEARCH_LOG_NOT_EXIST(400,"가게 검색어가 존재하지 않습니다."),
    NOT_WISHLIST(400,"위시리스트가 없습니다.");

    private HttpStatus httpStatus;

    private Integer errorCode;

    private String message;

    ERRORCODE(Integer errorCode,String message){
        this.errorCode = errorCode;
        this.message = message;
    }
}
