package com.example.coffies_vol_02.config.redis;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.like.domain.Like;
import com.example.coffies_vol_02.like.repository.LikeRepository;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedissonService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final LikeRepository likeRepository;

    //게시글 조회수 증가
    @DistributeLock(key = "#lockKey")
    public BoardResponse boardDetailReadCountUp(String lockKey, Integer boardId){
        BoardResponse response = Optional.ofNullable(boardRepository
                        .boardDetail(boardId))
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

        boardRepository.ReadCountUpToDB(boardId);

        return response;
    }

    //게시글 좋아요 증가.
    @DistributeLock(key = "#key")
    public void boardLikeUp(String key,Integer memberId,Integer boardId){
        Optional<Board>board = boardRepository.findById(boardId);
        Optional<Member>member = memberRepository.findById(memberId);
        //게시글내에 있는 좋아요 수증가.
        board.get().likeCountUp();
        Like like = new Like(member.get(), board.get());
        likeRepository.save(like);
    }

    //게시글 좋아요 감소.

    //가게 댓글 좋아요 증가.

    //가게 댓글 좋아요 감소.

}
