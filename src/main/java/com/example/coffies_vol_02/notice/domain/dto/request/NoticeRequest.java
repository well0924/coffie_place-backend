package com.example.coffies_vol_02.notice.domain.dto.request;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record NoticeRequest(
        @NotBlank
        String noticeGroup,
        @ApiModelProperty(dataType ="char")
        Character isFixed,
        String noticeTitle,
        String noticeWriter,
        @NotBlank
        String noticeContents,
        String fileGroupId,
        List<MultipartFile> files)implements Serializable {

        public NoticeBoard toEntity(NoticeBoard noticeBoard){
                return NoticeBoard
                        .builder()
                        .id(noticeBoard.getId())
                        .noticeTitle(noticeTitle)
                        .noticeWriter(noticeWriter)
                        .noticeContents(noticeContents)
                        .noticeGroup(noticeGroup)
                        .fileGroupId(fileGroupId)
                        .isFixed(isFixed)
                        .build();
        }

}
