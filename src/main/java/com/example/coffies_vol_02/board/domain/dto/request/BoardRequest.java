package com.example.coffies_vol_02.board.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@ApiModel(value = "게시글요청 record", description = "게시글 requestDto")
public record BoardRequest(
    @Schema(name = "boardTitle",type = "String")
    @NotBlank(message = "제목을 작성해 주세요.")
    String boardTitle,
    @Schema(name = "boardContents",type = "String")
    @NotBlank(message = "내용을 입력해 주세요.")
    String boardContents,
    @Schema(name = "boardAuthor",type = "String")
    String boardAuthor,
    @Schema(name = "readCount",type = "Integer")
    Integer readCount,
    @Schema(name = "passWd",type = "String")
    String passWd,
    @Schema(name = "fileGroupId",type = "String")
    String fileGroupId)implements Serializable {
}
