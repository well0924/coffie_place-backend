package com.example.coffies_vol_02.commnet.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "댓글 requestDto")
public record CommentRequest(
        @ApiModelProperty(name = "댓글 작성자",dataType = "String")
        String replyWriter,
        @ApiModelProperty(name = "댓글 내용",dataType = "String",required = true)
        @NotBlank
        String replyContents,
        @ApiModelProperty(name = "댓글 평점",dataType = "Integer")
        Integer replyPoint) {

}
