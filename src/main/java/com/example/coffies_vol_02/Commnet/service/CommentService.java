package com.example.coffies_vol_02.Commnet.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final PlaceRepository placeRepository;

    /**
    *   댓글 목록(자유게시판)
    **/
    @Transactional(readOnly = true)
    public List<CommentDto.CommentResponseDto> replyList(Integer boardId) throws Exception {

        List<Comment>list = commentRepository.findByBoardId(boardId);

        return list.stream().map(comment ->new CommentDto.CommentResponseDto(comment)).toList();
    }

    /**
    *   댓글 작성(자유게시판)
    **/
    @Transactional
    public Integer replyWrite(Integer boardId,Member member,CommentDto.CommentRequestDto dto){

        Optional<Board>boardDetail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = Comment
                .builder()
                .board(boardDetail.get())
                .replyWriter(member.getUserId())
                .replyContents(dto.getReplyContents())
                .member(member)
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }

    /**
    *   댓글 삭제(자유게시판)
    **/
    @Transactional
    public void commentDelete(Integer replyId,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        String userId = member.getUserId();
        String commentAuthor = comment.getReplyWriter();

        if(!userId.equals(commentAuthor)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        commentRepository.deleteById(replyId);
    }

    /**
     * 가게 댓글 목록
     * */
    @Transactional(readOnly = true)
    public List<CommentDto.PlaceCommentResponse>placeCommentList(Integer placeId) throws Exception {
        List<Comment>list = commentRepository.findByPlaceId(placeId);
        return list.stream().map(comment -> new CommentDto.PlaceCommentResponse(comment)).collect(Collectors.toList());
    }

    /**
     * 가게 댓글 작성
     * */
    @Transactional
    public Integer placeCommentWrite(Integer placeId,CommentDto.CommentRequestDto dto,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Place> detail = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        Comment comment = Comment
                .builder()
                .place(detail.get())
                .replyWriter(member.getUserId())
                .replyContents(dto.getReplyContents())
                .replyPoint(dto.getReplyPoint())
                .member(member)
                .build();

        commentRepository.save(comment);

        Integer insertResult = comment.getId();

        return insertResult;
    }

    /**
     * 가게 댓글 삭제
     * */
    @Transactional
    public void placeCommentDelete(Integer replyId,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        String userId = member.getUserId();
        String commentAuthor = comment.getReplyWriter();

        if(!userId.equals(commentAuthor)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        commentRepository.deleteById(replyId);
    }

    /**
     * 가게 댓글 평점 계산
     * */
    @Transactional
    public Double getStarAvgByPlaceId(@Param("id") Integer placeId) throws Exception {
        return commentRepository.getStarAvgByPlaceId(placeId);
    }

    /**
     * 가게 평점 출력
     * */
    @Transactional
    public void cafeReviewRate(@Param("rate")Double reviewRate,@Param("id")Integer placeId){
        commentRepository.cafeReviewRate(reviewRate,placeId);
    }

    /**
     * 가게 댓글 평점 출력
     * */
    @Transactional
    public void updateStar(Integer placeId)throws Exception{
       Double avgStar = getStarAvgByPlaceId(placeId);
       cafeReviewRate(avgStar, placeId);
    }
}
