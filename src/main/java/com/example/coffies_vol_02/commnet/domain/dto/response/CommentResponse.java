package com.example.coffies_vol_02.commnet.domain.dto.response;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CommentResponse(
        Integer id,
        String replyWriter,
        String replyContents,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime createdTime) {

    public CommentResponse (Comment comment){
        this(comment.getId(),comment.getMember().getUserId(),comment.getReplyContents(),comment.getCreatedTime());
    }
}
