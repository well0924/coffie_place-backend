package com.example.coffies_vol_02.like.service;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.like.domain.Like;
import com.example.coffies_vol_02.like.repository.CommentLikeRepository;
import com.example.coffies_vol_02.like.repository.LikeRepository;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final CommentLikeRepository commentLikeRepository;
    public static final String LikeSuccess ="좋아요 추가";
    public static final String LikeCancel ="좋아요 취소";
    private final PlaceRepository placeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public boolean hasLikeBoard(Board board, Member member){
        return likeRepository.findByMemberAndBoard(member,board).isPresent();
    }

    public String createBoardLike(Integer boardId,Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));

        //좋아요 중복체크를 거친 뒤에 중복되지 않으면 카운트,
        if(!hasLikeBoard(detail.orElse(null),member)){
            likeRepository.save(Like.builder().member(member).board(detail.orElseThrow()).build());
        }
        return LikeSuccess;
    }

    public String cancelLike(Integer boardId, Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));

        Optional<Like> like = Optional.ofNullable(likeRepository.findByMemberAndBoard(member, detail.orElseThrow()).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.LIKE_NOT_FOUND)));

        if(like.isPresent()){
            likeRepository.delete(like.get());
        }
        return LikeCancel;
    }

    public List<String>likeCount(Integer boardId,Member member){
        Board board = boardRepository.findById(boardId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST));

        Integer likeCount = likeRepository.countByBoard(board).orElse(0);

        List<String> resultData = new ArrayList<>(Collections.singletonList(String.valueOf(likeCount)));

        if (Objects.nonNull(member)) {
            resultData.add(String.valueOf(hasLikeBoard(board, member)));
            return resultData;
        }
        return resultData;
    }

    public boolean hasCommentLike(Member member,Comment comment){
        return commentLikeRepository.findByMemberAndComment(member, comment).isPresent();
    }

    public String commentLikePlus(Integer placeId,Integer replyId,Member member){
        Optional<Place>placeDetail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        Optional<Comment>commentDetail = Optional.ofNullable(commentRepository.findById(replyId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_REPLY)));

        if(!hasCommentLike(member,commentDetail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY)))){
            commentLikeRepository.save(new CommentLike(member,commentDetail.get()));
        }
        return LikeSuccess;
    }

    public String commentLikeMinus(Integer placeId,Integer replyId,Member member){
        Optional<Place>placeDetail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        Optional<Comment>commentDetail = Optional.ofNullable(commentRepository.findById(replyId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_REPLY)));

        Optional<CommentLike>commentLike = Optional.of(commentLikeRepository
                .findByMemberAndComment(member, commentDetail
                        .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY)))
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.LIKE_NOT_FOUND)));

        commentLikeRepository.delete(commentLike.get());

        return LikeCancel;
    }

    public List<String>likeCommentCount(Integer replyId,Member member){
        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST));

        Integer likeCount = commentLikeRepository.countByComment(comment).orElse(0);

        List<String> resultData = new ArrayList<>(Collections.singletonList(String.valueOf(likeCount)));

        if (Objects.nonNull(member)) {
            resultData.add(String.valueOf(hasCommentLike(member,comment)));
            return resultData;
        }
        return resultData;
    }
}
