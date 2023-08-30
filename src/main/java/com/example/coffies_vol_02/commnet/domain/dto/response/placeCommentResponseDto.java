package com.example.coffies_vol_02.commnet.domain.dto.response;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel(value = "댓글 응답 dto",description = "댓글 응답 dto")
@Getter
@ToString
@AllArgsConstructor
public class placeCommentResponseDto {
    @Schema(name = "댓글 번호",description = "댓글 번호",type = "Integer")
    private Integer id;
    @Schema(name = "댓글 작성자",description = "댓글 작성자",type = "String")
    private String replyWriter;
    @Schema(name = "댓글 내용",description = "댓글 내용",type = "String")
    private String replyContents;
    @Schema(name = "댓글 평점",description = "댓글 평점",type = "Integer")
    private Integer reviewPoint;
    @Schema(name = "댓글 좋아요",description = "댓글 좋아요",type = "Integer")
    private Integer liked;
    @Schema(name = "댓글 작성일",description = "댓글 작성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;

    @Builder
    public placeCommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.replyWriter = comment.getMember().getUserId();
        this.replyContents  = comment.getReplyContents();
        this.reviewPoint = comment.getReplyPoint();
        this.liked = comment.getLikes().size();
        this.createdTime = comment.getCreatedTime();
    }
}
