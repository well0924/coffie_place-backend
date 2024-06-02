package com.example.coffies_vol_02.factory;

import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.like.domain.Like;

public class LikeFactory {


    public static CommentLike getCommentLike(){
        return CommentLike
                .builder()
                .member(MemberFactory.memberDto())
                .comment(CommentFactory.comment())
                .build();
    }
    public static Like getLike(){
        return Like
                .builder()
                .board(BoardFactory.board())
                .member(MemberFactory.memberDto())
                .build();
    }
}
