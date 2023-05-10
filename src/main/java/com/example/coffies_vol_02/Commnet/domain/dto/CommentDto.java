package com.example.coffies_vol_02.Commnet.domain.dto;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentRequestDto implements Serializable {
        private String replyWriter;
        private String replyContents;
        private Integer replyPoint;
    }
    @Getter
    @NoArgsConstructor
    public static class CommentResponseDto implements Serializable{
        private Integer id;
        private String replyWriter;
        private String replyContents;
        private Integer reviewPoint;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime updatedTime;

        @Builder
        public CommentResponseDto (Comment comment){
            this.id = comment.getId();
            this.replyWriter = comment.getMember().getUserId();
            this.replyContents  = comment.getReplyContents();
            this.reviewPoint = comment.getReplyPoint();
            this.createdTime = comment.getCreatedTime();
            this.updatedTime = comment.getUpdatedTime();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceCommentResponse{
        private Integer id;
        private String replyWriter;
        private String replyContents;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime updatedTime;

        @Builder
        public PlaceCommentResponse(Comment comment){
            this.id = comment.getId();
            this.replyWriter = comment.getMember().getUserId();
            this.replyContents = comment.getReplyContents();
            this.createdTime = comment.getCreatedTime();
            this.updatedTime = comment.getUpdatedTime();
        }
    }
}
