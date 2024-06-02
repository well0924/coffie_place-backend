package com.example.coffies_vol_02.factory;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;

public class CommentFactory {
    public static Comment comment(){
        return Comment
                .builder()
                .replyContents("reply test")
                .replyWriter(MemberFactory.memberDto().getUserId())
                .replyPoint(3)
                .board(BoardFactory.board())
                .member(MemberFactory.memberDto())
                .place(PlaceFactory.place())
                .build();
    }

    public static CommentRequest RequestDto(){
        return new CommentRequest(
                comment().getReplyWriter(),
                comment().getReplyContents(),
                comment().getReplyPoint());
    }
    public static placeCommentResponseDto placeCommentResponseDto(){
        return placeCommentResponseDto
                .builder()
                .comment(comment())
                .build();
    }
}
