package com.example.coffies_vol_02.Like.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Like.repository.LikeRepository;
import com.example.coffies_vol_02.Member.domain.Member;
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

    /*
    *  게시글 좋아요 중복
    */
    @Transactional
    public boolean hasLikeBoard(Board board, Member member){
        return likeRepository.findByMemberAndBoard(member,board).isEmpty();
    }

    /*
    * 게시글 좋아요 +1
    */
    public String createBoardLike(Integer boardId,Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));

        //좋아요 중복체크를 거친 뒤에 중복되지 않으면 카운트,
        if(hasLikeBoard(detail.get(),member)){
            likeRepository.save(new Like(member, detail.get()));
        }
        return LikeSuccess;
    }

    /*
     * 게시글 좋아요 -1
     */
    public String cancelLike(Integer boardId, Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST)));

        Optional<Like> like = Optional.ofNullable(likeRepository.findByMemberAndBoard(member, detail.get()).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.LIKE_NOT_FOUND)));

        likeRepository.delete(like.get());

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

}
