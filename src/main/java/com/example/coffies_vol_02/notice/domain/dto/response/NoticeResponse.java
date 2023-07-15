package com.example.coffies_vol_02.notice.domain.dto.response;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel(value = "공지게시판 응답 record",description = "공지게시판 응답Dto")
public record NoticeResponse(
        @Schema(description = "공지게시판 번호")
        Integer id,
        @Schema(description = "공지게시글 종류")
        String noticeGroup,
        @Schema(description = "공지게시글 제목")
        String noticeTitle,
        @Schema(description = "공지게시글 작성자")
        String noticeWriter,
        @Schema(description = "공지게시글 내용")
        String noticeContents,
        @Schema(description = "공지게시글 파일 그룹아이디")
        String fileGroupId,
        @Schema(description = "공지게시글 고정여부")
        Character isFixed,
        @Schema(description = "공지게시글 작성일")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime createdTime,
        @Schema(description = "공지게시글 수정일")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime updatedTime) implements Serializable{

    public NoticeResponse(NoticeBoard noticeBoard){
        this(
                noticeBoard.getId(),
                noticeBoard.getNoticeGroup(),
                noticeBoard.getNoticeTitle(),
                noticeBoard.getNoticeWriter(),
                noticeBoard.getNoticeContents(),
                noticeBoard.getFileGroupId(),
                noticeBoard.getIsFixed(),
                noticeBoard.getCreatedTime(),
                noticeBoard.getUpdatedTime()
        );
    }
}
