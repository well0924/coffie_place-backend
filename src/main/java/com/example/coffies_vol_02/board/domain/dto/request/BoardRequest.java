package com.example.coffies_vol_02.board.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Schema(description = "게시글 requestDto")
public record BoardRequest(
    @Schema(description = "게시글 제목")
    @NotBlank(message = "제목을 작성해 주세요.")
    String boardTitle,
    @Schema(description = "게시글 내용")
    @NotBlank(message = "내용을 입력해 주세요.")
    String boardContents,
    @Schema(description = "게시글 작성자")
    String boardAuthor,
    @Schema(description = "게시글 조회수")
    Integer readCount,
    @Schema(description = "게시글 비밀번호")
    String passWd,
    @Schema(description = "게시글 fileGroupId")
    String fileGroupId,
    @Schema(description = "게시글 첨부파일")
    List<MultipartFile> files)implements Serializable {
}
