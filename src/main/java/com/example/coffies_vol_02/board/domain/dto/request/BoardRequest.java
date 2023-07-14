package com.example.coffies_vol_02.board.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@ApiModel(value = "게시글 요청 record",description = "게시글 requestDto")
public record BoardRequest(
    @ApiModelProperty(name = "게시글 제목",dataType = "String")
    @NotBlank(message = "제목을 작성해 주세요.")
    String boardTitle,
    @ApiModelProperty(name = "게시글 내용",dataType = "String")
    @NotBlank(message = "내용을 입력해 주세요.")
    String boardContents,
    @ApiModelProperty(name = "게시글 작성자",dataType = "String")
    String boardAuthor,
    @ApiModelProperty(name = "게시글 조회수",dataType = "Integer")
    Integer readCount,
    @ApiModelProperty(name = "게시글 비밀번호",dataType = "String")
    String passWd,
    @ApiModelProperty(name = "게시글 fileGroupId",dataType = "String")
    String fileGroupId,
    @ApiModelProperty(name = "게시글 첨부파일",dataType ="List")
    List<MultipartFile> files)implements Serializable {
}
