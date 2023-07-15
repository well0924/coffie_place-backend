package com.example.coffies_vol_02.commnet.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
@ApiModel(value = "댓글 requestDto",description = "댓글 요청 dto")
public record CommentRequest(
        @Schema(name = "replyWriter",description = "댓글 작성자",type = "String")
        String replyWriter,
        @Schema(name = "replyContents",description = "댓글 내용",type = "String",required = true)
        @NotBlank
        String replyContents,
        @Schema(name = "replyPoint",description = "댓글 평점",type = "Integer")
        Integer replyPoint) {

}
