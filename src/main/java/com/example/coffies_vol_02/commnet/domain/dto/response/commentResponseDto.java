package com.example.coffies_vol_02.commnet.domain.dto.response;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
public class commentResponseDto {
    private Integer id;
    private String replyWriter;
    private String replyContents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;

    @Builder
    public commentResponseDto(Comment comment){
        this.id = comment.getId();
        this.replyWriter = comment.getMember().getUserId();
        this.replyContents  = comment.getReplyContents();
        this.createdTime = comment.getCreatedTime();
    }
}