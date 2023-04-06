package com.example.coffies_vol_02.Commnet.domain.dto;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentRequestDto{
        private String replyWriter;
        private String replyContents;
    }
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponseDto{
        private Integer id;
        private String replyWriter;
        private String replyContents;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime createdTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime updatedTime;

        @Builder
        public CommentResponseDto(Comment comment){
            this.id = comment.getId();
            this.replyWriter = comment.getMember().getUserId();
            this.replyContents  = comment.getReplyContents();
            this.createdTime = comment.getCreatedTime();
            this.updatedTime = comment.getUpdatedTime();
        }
    }
}
