package com.example.coffies_vol_02.notice.domain.dto.response;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "공지게시판 응답Dto")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {
    private Integer id;
    private String noticeGroup;
    private String noticeTitle;
    private String noticeWriter;
    private String noticeContents;
    private String fileGroupId;
    private Character isFixed;
    private LocalDateTime createdTime;
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
