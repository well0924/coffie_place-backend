package com.example.coffies_vol_02.commnet.service;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
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
import java.util.stream.Stream;

@Log4j2
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final PlaceRepository placeRepository;

    /**
     * 댓글 목록(자유게시판)
     * @author 양경빈
     * @param boardId 자유게시판 게시글 번호 번호가 없는 경우에는 BOARD_NOT_FOUND 를 발생
     * @return list 자유게시판 댓글 목록
     **/
    @Transactional(readOnly = true)
    public List<placeCommentResponseDto> replyList(Integer boardId) throws Exception {

        List<Comment>list = commentRepository.findByBoardId(boardId);

        return list.stream()
                .flatMap(comment-> Stream.of(new placeCommentResponseDto(comment)))
                .collect(Collectors.toList());
    }

    /**
     * 댓글 작성(자유게시판)
     * @author 양경빈
     * @param boardId 자유게시글 번호 게시글 번호가 없는 경우에는 BOARD_NOT_FOUND 발생
     * @param member 로그인 인증에 필요한 객체 로그인이 안된 경우에는 ONLY_USER 발생
     * @param dto 댓글 작성에 필요한 record 객체
     * @exception CustomExceptionHandler 로그인이 안된 경우,조회된 자유게시글이 없는 경우 예외 발생
     * @see BoardRepository#findById(Object) 게시글을 조회하는 메서드
     * @see CommentRepository#save(Object) 댓글을 저장하는 메서드
     * @return comment.getId() 자유게시판 댓글 번호
     **/
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

    /**
     * 댓글 삭제(자유게시판)
     * @author 양경빈
     * @param replyId 자유게시글 댓글번호
     * @param member 로그인 인증에 필요한 객체 로그인이 안된 경우에는 ONLY_USER 발생
     * @exception CustomExceptionHandler 로그인이 안된 경우,조회된 자유게시판에 댓글이 없는 경우(NOT_REPLY),로그인한 회원과 댓글 작성자가 다른 경우(NOT_AUTH)
     * @see CommentRepository#findById(Object) 댓글을 조회하는 메서드
     * @see CommentRepository#deleteById(Object) 댓글을 삭제하는 메서드
     * @return comment.getId() 자유게시판 댓글 번호
     **/
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

    /**
     * 가게 댓글 목록
     * @author 양경빈
     * @param placeId 가게 번호
     * @see CommentRepository#findByPlaceId(Integer) 가게번호로 가게댓글을 조회하는 메서드
     * @return list 가게댓글 목록
     **/
    @Transactional(readOnly = true)
    public List<placeCommentResponseDto>placeCommentList(Integer placeId) throws Exception {
        List<Comment>list = commentRepository.findByPlaceId(placeId);
        return list.stream().flatMap(comment-> Stream.of(new placeCommentResponseDto(comment))).collect(Collectors.toList());
    }

    /**
     * 가게 댓글 작성
     * @author 양경빈
     * @param placeId 가게 번호 가게조회시 번호가 없는 경우에는 PLACE_NOT_FOUND 발생
     * @param dto 댓글 작성에 필요한 dto
     * @param member 로그인 인증에 필요한 객체 인증에 실패한 경우에는 ONLY_USER 발생
     * @exception CustomExceptionHandler 조회할 가게가 없는 경우,로그인이 인증 안되는 경우
     * @return comment.getId() 가게 댓글 번호
     * @see PlaceRepository#findById(Object) 가게를 조회하는 메서드
     * @see CommentRepository#save(Object) 댓글을 저장하는 메서드
     **/
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

    /**
     * 가게 댓글 삭제
     *
     * @param replyId 가게 댓글번호
     * @param member  로그인 인증에 필요한 객체
     * @return
     * @throws CustomExceptionHandler 조회할 가게가 없는 경우,로그인이 인증 안되는 경우,댓글 작성자와 로그인한 회원이 일치하지 않은 경우(NOT_AUTH)
     * @author 양경빈
     * @see CommentRepository#findById(Object) 가게댓글을 조회하는 메서드
     * @see CommentRepository#deleteById(Object) 가게댓글을 삭제하는 메서드
     */
    @Transactional
    public Object placeCommentDelete(Integer replyId, Member member){

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
        return null;
    }

    /**
     * 가게 댓글 평점 계산
     * @author 양경빈
     * @param placeId 가게 번호
     * @see CommentRepository#getStarAvgByPlaceId(Integer) 가게 댓글 평점을 조회하는 메서드
     * @return Double 가게 댓글 평점
     **/
    @Transactional
    public Double getStarAvgByPlaceId(@Param("id") Integer placeId) throws Exception {
        return commentRepository.getStarAvgByPlaceId(placeId);
    }

    /**
     * 가게 평점 출력
     * @author 양경빈
     * @param reviewRate 댓글평점
     * @param placeId 가게 번호
     * @see CommentRepository#cafeReviewRate(Double, Integer) 가게 댓글 평점을 출력하는 메서드
     **/
    @Transactional
    public void cafeReviewRate(@Param("rate")Double reviewRate,@Param("id")Integer placeId){
        commentRepository.cafeReviewRate(reviewRate,placeId);
    }

    /**
     * 가게 댓글 평점 반영
     * @author 양경빈
     * @param placeId 가게번호
     **/
    @Transactional
    public void updateStar(Integer placeId)throws Exception{
       Double avgStar = getStarAvgByPlaceId(placeId);
       cafeReviewRate(avgStar, placeId);
    }

    /**
     * 최근에 작성한 댓글(limit5)
     * @author 양경빈
     * @return List<placeCommentResponseDto>
     **/
    public List<placeCommentResponseDto>recentCommentTop5(){
        return commentRepository.findTop5ByOrderByCreatedTimeDesc();
    }
}
