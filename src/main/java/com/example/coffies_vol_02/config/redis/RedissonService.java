package com.example.coffies_vol_02.config.redis;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
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
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Component
@Transactional
@RequiredArgsConstructor
public class RedissonService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final LikeRepository likeRepository;

    private final CommentRepository commentRepository;

    private final CommentLikeRepository commentLikeRepository;

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

        //게시글 내에 있는 좋아요수 증가.
        board.orElseThrow().likeCountUp();
        Like like = new Like(member.orElseThrow(), board.orElseThrow());
        likeRepository.save(like);
    }

    //게시글 좋아요 감소.
    @DistributeLock(key = "#key")
    public void boardLikeDown(String key,Integer memberId,Integer boardId){
        Optional<Board>board = boardRepository.findById(boardId);
        Optional<Member>member = memberRepository.findById(memberId);

        Like like = Like
                .builder()
                .member(member.orElseThrow())
                .board(board.orElseThrow())
                .build();

        //게시글 좋아요수 감소
        board.get().likeCountDown();

        likeRepository.save(like);
    }

    //가게 댓글 좋아요 증가.
    @DistributeLock(key = "#key")
    public void placeCommentLikeUp(String key,Integer memberId,Integer commentId){
        Optional<Comment>comment = Optional
                .ofNullable(commentRepository.findById(commentId)
                        .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_REPLY)));

        Optional<Member>member = Optional
                .ofNullable(memberRepository.findById(memberId)
                        .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        //댓글 좋아요 유무
        Optional<CommentLike> existingLike = commentLikeRepository
                .findByMemberAndComment(member.orElseThrow(), comment.orElseThrow());

        if(existingLike.isEmpty() && !existingLike.isPresent()){
            CommentLike commentLike = CommentLike
                    .builder()
                    .comment(comment.orElseThrow())
                    .member(member.orElseThrow())
                    .build();
            //댓글 좋아요수 증가.
            comment.get().commentLikeUp();
            //좋아요 저장
            commentLikeRepository.save(commentLike);
        } else {
            log.info("Member {} has already liked comment {}", memberId, commentId);
        }
    }

    //가게 댓글 좋아요 감소.
    @DistributeLock(key = "#key")
    public void placeCommentLikeDown(String key,Integer memberId,Integer commentId){

        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        //댓글 좋아요 유무
        CommentLike existingLike = commentLikeRepository
                .findByMemberAndComment(member, comment).orElse(null);

        log.info(existingLike.toString());
        log.info(existingLike.getComment().getId());
        log.info(existingLike.getMember().getId());

        if (existingLike != null) {
            log.info("들어왔음??");
            Comment commentEntity = comment;
            log.info("comment:::"+commentEntity.getLikeCount());

            if (commentEntity.getLikeCount() > 0) {
                commentEntity.commentLikeDown();
                log.info("like Count ::" + commentEntity.getLikeCount());
                log.info("likeId::::"+existingLike.getId());
                commentLikeRepository.deleteByCommentLike(existingLike.getId());
            } else {
                log.info("Comment like count is already zero and cannot be decreased further");
                log.info("likeId::::"+existingLike.getId());
                commentLikeRepository.deleteByCommentLike(existingLike.getId());
            }
        } else {
            log.info("Member {} has not liked comment {}", memberId, commentId);
        }
        log.info(existingLike);
        commentLikeRepository.delete(existingLike);  // 기존의 CommentLike 객체 삭제
        commentLikeRepository.flush();
        log.info(existingLike);
    }
}
