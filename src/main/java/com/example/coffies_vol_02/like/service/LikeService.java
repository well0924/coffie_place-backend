package com.example.coffies_vol_02.like.service;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.config.redis.RedissonService;
import com.example.coffies_vol_02.like.repository.CommentLikeRepository;
import com.example.coffies_vol_02.like.repository.LikeRepository;
import com.example.coffies_vol_02.member.domain.Member;
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

    private final CommentRepository commentRepository;

    private final RedissonService redissonService;

    /**
     * 게시글 좋아요 중복 확인
     * @author 양경빈
     * @param board 게시글 객체
     * @param member 회원 객체
     **/
    @Transactional
    public boolean hasLikeBoard(Board board, Member member){
        return likeRepository.findByMemberAndBoard(member,board).isPresent();
    }

    /**
     * 게시글 좋아요 추가
     * @author 양경빈
     * @param boardId 게시글 번호
     * @param memberId 회원 번호
     **/
    public void boardLikePlus(Integer boardId,Integer memberId){
        String key = CacheKey.LIKES;
        redissonService.boardLikeUp(key,memberId,boardId);
    }

    /**
     * 게시글 좋아요 감소
     * @author 양경빈
     * @param boardId 게시글 번호
     * @param memberId 회원 번호
     **/
    public void boardLikeMinus(Integer boardId, Integer memberId){
        String key = CacheKey.LIKES;
        redissonService.boardLikeDown(key,memberId,boardId);
    }

    /**
     * 게시글 좋아요 갯수
     * @author 양경빈
     * @param boardId 게시글 번호
     * @param member 회원 객체
     **/
    public List<String>likeCount(Integer boardId,Member member){
        Board board = boardRepository.findById(boardId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

        Integer likeCount = likeRepository.countByBoard(board).orElse(0);

        List<String> resultData = new ArrayList<>(Collections.singletonList(String.valueOf(likeCount)));

        if (Objects.nonNull(member)) {
            resultData.add(String.valueOf(hasLikeBoard(board, member)));
            return resultData;
        }
        return resultData;
    }

    /**
     * 가게 댓글 좋아요 확인
     * @author 양경빈
     * @param comment 댓글 객체
     * @param member 회원 객체
     **/
    public boolean hasCommentLike(Member member,Comment comment){
        return commentLikeRepository.findByMemberAndComment(member, comment).isPresent();
    }

    /**
     * 가게 댓글 좋아요 증가
     * @author 양경빈
     * @param replyId 댓글 번호
     * @param member 회원 객체
     **/
    public void commentLikePlus(Integer replyId,Member member){
        String key = CacheKey.LIKES;
        redissonService.placeCommentLikeUp(key, member.getId(), replyId);
    }

    /**
     * 가게 댓글 좋아요 감소
     * @author 양경빈
     * @param replyId 댓글 번호
     * @param member 회원 객체
     **/
    public void commentLikeMinus(Integer replyId,Member member){
        String key = CacheKey.LIKES;
        redissonService.placeCommentLikeDown(key,member.getId(),replyId);
    }

    /**
     * 가게 댓글 좋아요 갯수
     * @author 양경빈
     * @param replyId 댓글 번호
     * @param member 회원 객체
     **/
    public List<String>likeCommentCount(Integer replyId,Member member){
        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        Integer likeCount = commentLikeRepository.countByComment(comment).orElse(0);

        List<String> resultData = new ArrayList<>(Collections.singletonList(String.valueOf(likeCount)));

        if (Objects.nonNull(member)) {
            resultData.add(String.valueOf(hasCommentLike(member,comment)));
            return resultData;
        }
        return resultData;
    }
}
