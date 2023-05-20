package com.example.coffies_vol_02.notice.domain.dto.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "공지게시판 요청 Dto")
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {
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
