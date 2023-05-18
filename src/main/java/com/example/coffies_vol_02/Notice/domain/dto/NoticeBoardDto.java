package com.example.coffies_vol_02.Notice.domain.dto;

import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class NoticeBoardDto {
    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRequestDto{
        @NotBlank
        private String noticeGroup;
        @ApiModelProperty(dataType ="char")
        private Character isFixed;
        private String noticeTitle;
        private String noticeWriter;
        @NotBlank
        private String noticeContents;
        private String fileGroupId;
        private List<MultipartFile> files;
    }
    @Getter
    @ToString
    @NoArgsConstructor
    public static class BoardResponseDto{
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
        public BoardResponseDto(NoticeBoard noticeBoard){
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
}
