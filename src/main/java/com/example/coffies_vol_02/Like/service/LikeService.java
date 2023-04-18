package com.example.coffies_vol_02.Like.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Like.repository.LikeRepository;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    public static final String LikeSuccess ="좋아요 추가";
    public static final String LikeCancel ="좋아요 취소";
    private final CommentRepository commentRepository;
    private final PlaceRepository placeRepository;

    /*
    *  게시글 좋아요 중복
    */
    @Transactional
    public boolean hasLikeBoard(Board board, Member member){
        return likeRepository.findByMemberAndBoard(member,board).isPresent();
    }

    /*
    * 게시글 좋아요 +1
    */
    public String createBoardLike(Integer boardId,Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));

        //좋아요 중복체크를 거친 뒤에 중복되지 않으면 카운트,
        if(hasLikeBoard(detail.get(),member) == false){
            likeRepository.save(new Like(member, detail.get()));
        }else if(hasLikeBoard(detail.get(),member)== true){
            cancelLike(detail.get().getId(),member);
            return LikeCancel;
        }
        return LikeSuccess;
    }

    /*
     * 게시글 좋아요 -1
     */
    public String cancelLike(Integer boardId, Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));

        Optional<Like> like = Optional.ofNullable(likeRepository.findByMemberAndBoard(member, detail.get()).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.LIKE_NOT_FOUND)));

        if(like.isPresent()){
            likeRepository.delete(like.get());
        }else{
            createBoardLike(boardId,member);
            return LikeSuccess;
        }
        return LikeCancel;
    }

    /*
     * 게시글 좋아요 카운트
     */
    public List<String>likeCount(Integer boardId,Member member){
        Board board = boardRepository.findById(boardId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST));

        Integer likeCount = likeRepository.countByBoard(board).orElse(0);

        List<String> resultData = new ArrayList<>(Arrays.asList(String.valueOf(likeCount)));

        if (Objects.nonNull(member)) {
            resultData.add(String.valueOf(hasLikeBoard(board, member)));
            return resultData;
        }
        return resultData;
    }

    @Transactional
    public boolean hasLikeComment(Member member, Comment comment){
        return likeRepository.findByMemberAndComment(member,comment).isEmpty();
    }

    /*
    * 가게 댓글 좋아요
    */
    public String placeCommentLike(Integer replyId,Member member){
        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));
        if(hasLikeComment(member,comment)){
            likeRepository.save(new Like(member,comment));
        }
        return LikeSuccess;
    }

    /*
    * 가게 댓글 취소
    */
    public String cancelCommentLike(Integer replyId, Member member){
        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));
        Optional<Like> like = Optional.ofNullable(likeRepository.findByMemberAndComment(member, comment).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.LIKE_NOT_FOUND)));

        likeRepository.delete(like.get());

        return LikeCancel;
    }
}
