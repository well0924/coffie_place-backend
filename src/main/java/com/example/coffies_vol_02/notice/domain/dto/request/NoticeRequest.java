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
        @NotBlank(message = "공지 제목을 적어주세요.")
        String noticeTitle,
        String noticeWriter,
        @NotBlank(message = "공지 내용을 적어주세요.")
        String noticeContents,
        String fileGroupId,
        List<MultipartFile>files)implements Serializable {

}
