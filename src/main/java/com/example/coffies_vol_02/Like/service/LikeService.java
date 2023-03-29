package com.example.coffies_vol_02.Like.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Like.repository.LikeRepository;
import com.example.coffies_vol_02.config.Exception.ERRORCODE;
import com.example.coffies_vol_02.config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private static final String likeMessage ="좋아요 처리 완료";
    private static final String likeCancelMessage ="좋아요 취소 처리 완료";
    /*
    *  좋아요 중복
    */
    @Transactional
    public boolean hasLikeBoard(Board board, Member member){
        return likeRepository.findByMemberAndBoard(member,board).isPresent();
    }
    /*
    * 좋아요 +1
    */
    @Transactional
    public String createLikeBoard(Board board,Member member){
        board.increaseLikeCount();
        Like like = new Like(member,board);
        likeRepository.save(like);
        return likeMessage;
    }
    /*
    * 좋아요 -1
    */
    @Transactional
    public String removeLikeBoard(Board board,Member member){
        Like likeBoard = likeRepository.findByMemberAndBoard(member,board).orElseThrow(()->{throw new CustomExceptionHandler(ERRORCODE.LIKE_NOT_FOUND);});
        board.decreaseLikeCount();
        likeRepository.delete(likeBoard);
        return likeCancelMessage;
    }
}
