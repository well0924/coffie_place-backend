package com.example.coffies_vol_02.notice.domain.dto.request;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
@Schema(description = "공지게시판 request dto")
public record NoticeRequest(
        @Schema(description = "공지게시판 종류",example = "자유게시판")
        @NotBlank
        String noticeGroup,
        @Schema(description = "게시글 고정 유무")
        @ApiModelProperty(dataType ="char")
        Character isFixed,
        @Schema(description = "공지 게시글 제목")
        @NotBlank(message = "공지 제목을 적어주세요.")
        String noticeTitle,
        @Schema(description = "공지 게시글 작성자")
        String noticeWriter,
        @Schema(description = "공지 게시글 내용")
        @NotBlank(message = "공지 내용을 적어주세요.")
        String noticeContents,
        @Schema(description = "파일 그룹 아이디")
        String fileGroupId)implements Serializable {

}
