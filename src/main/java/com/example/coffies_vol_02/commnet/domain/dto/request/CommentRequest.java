package com.example.coffies_vol_02.commnet.domain.dto.request;

public record CommentRequest(
        String replyWriter,
        String replyContents,
        Integer replyPoint) {

}
