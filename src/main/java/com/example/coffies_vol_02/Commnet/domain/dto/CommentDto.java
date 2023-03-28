package com.example.coffies_vol_02.Commnet.domain.dto;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import lombok.*;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    @ToString
    public static class CommentRequestDto{
        private String replyWriter;
        private String replyContents;
        private String replyPoint;
        private String replyLike;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponseDto{
        private Integer id;
        private String replyWriter;
        private String replyContents;
        private String replyLike;
        private String replyPoint;
        private LocalDateTime createdTime;
        private LocalDateTime updatedTime;
        @Builder
        public CommentResponseDto(Comment comment){
            this.id = comment.getId();
            this.replyWriter = comment.getMember().getUserId();
            this.replyContents  = comment.getReplyContents();
            this.replyLike = comment.getReplyLike();
            this.replyPoint = comment.getReplyPoint();
            this.createdTime = comment.getCreatedTime();
            this.updatedTime = comment.getUpdatedTime();
        }
    }
}
