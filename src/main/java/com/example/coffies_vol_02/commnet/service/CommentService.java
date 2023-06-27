package com.example.coffies_vol_02.commnet.service;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<placeCommentResponseDto> replyList(Integer boardId) throws Exception {

        List<Comment>list = commentRepository.findByBoardId(boardId);

        return list.stream().map(placeCommentResponseDto::new).toList();
    }

    @Transactional
    public Integer commentCreate(Integer boardId, Member member, CommentRequest dto){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>boardDetail = Optional.ofNullable(boardRepository
                .findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Comment comment = Comment
                .builder()
                .board(boardDetail.get())
                .replyWriter(member.getUserId())
                .replyContents(dto.replyContents())
                .member(member)
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    public void commentDelete(Integer replyId,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = commentRepository
                .findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        String userId = member.getUserId();
        String commentAuthor = comment.getReplyWriter();

        if(!userId.equals(commentAuthor)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        commentRepository.deleteById(replyId);
    }

    @Transactional(readOnly = true)
    public List<placeCommentResponseDto>placeCommentList(Integer placeId) throws Exception {
        List<Comment>list = commentRepository.findByPlaceId(placeId);
        return list.stream().map(placeCommentResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public Integer placeCommentCreate(Integer placeId, CommentRequest dto, Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Place> detail = Optional.ofNullable(placeRepository
                .findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        Comment comment = Comment
                .builder()
                .place(detail.get())
                .replyWriter(member.getUserId())
                .replyContents(dto.replyContents())
                .replyPoint(dto.replyPoint())
                .member(member)
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    public void placeCommentDelete(Integer replyId,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = commentRepository
                .findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        String userId = member.getUserId();
        String commentAuthor = comment.getReplyWriter();

        if(!userId.equals(commentAuthor)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        commentRepository.deleteById(replyId);
    }

    @Transactional
    public Double getStarAvgByPlaceId(@Param("id") Integer placeId) throws Exception {
        return commentRepository.getStarAvgByPlaceId(placeId);
    }

    @Transactional
    public void cafeReviewRate(@Param("rate")Double reviewRate,@Param("id")Integer placeId){
        commentRepository.cafeReviewRate(reviewRate,placeId);
    }

    @Transactional
    public void updateStar(Integer placeId)throws Exception{
       Double avgStar = getStarAvgByPlaceId(placeId);
       cafeReviewRate(avgStar, placeId);
    }

}
