package com.example.coffies_vol_02.notice.domain.dto.response;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "공지게시판 응답Dto")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto implements Serializable {
    @Schema(description = "공지게시판 번호")
    private Integer id;
    @Schema(description = "공지게시 종류")
    private String noticeGroup;
    @Schema(description = "공지게시글 제목")
    private String noticeTitle;
    @Schema(description = "공지게시글 작성자")
    private String noticeWriter;
    @Schema(description = "공지게시글 내용")
    private String noticeContents;
    @Schema(description = "공지게시글 파일 그룹아이디")
    private String fileGroupId;
    @Schema(description = "공지게시글 고정여부")
    private Character isFixed;
    @Schema(description = "공지게시글 작성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @Schema(description = "공지게시글 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;
    
    @Builder
    public NoticeResponseDto(NoticeBoard noticeBoard){
        this.id = noticeBoard.getId();
        this.fileGroupId = noticeBoard.getFileGroupId();
        this.noticeTitle = noticeBoard.getNoticeTitle();
        this.noticeWriter = noticeBoard.getNoticeWriter();
        this.noticeGroup = noticeBoard.getNoticeGroup();
        this.noticeContents = noticeBoard.getNoticeContents();
        this.isFixed = noticeBoard.getIsFixed();
        this.createdTime = noticeBoard.getCreatedTime();
        this.updatedTime = noticeBoard.getUpdatedTime();
    }
}
