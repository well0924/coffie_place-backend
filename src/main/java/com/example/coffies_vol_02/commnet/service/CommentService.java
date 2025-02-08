package com.example.coffies_vol_02.commnet.service;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final BoardRepository boardRepository;

    private final PlaceRepository placeRepository;

    private final RedisService redisService;

    /**
     * 댓글 목록(자유게시판)
     * @author 양경빈
     * @param boardId 자유게시판 게시글 번호 번호가 없는 경우에는 BOARD_NOT_FOUND 를 발생
     * @return list 자유게시판 댓글 목록
     **/
    @Transactional(readOnly = true)
    public List<placeCommentResponseDto> freeBoardCommentList(Integer boardId) throws Exception {

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
    public Integer createFreeBoardComment(Integer boardId, Member member, CommentRequest dto){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>boardDetail = Optional.ofNullable(boardRepository
                .findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Comment comment = Comment
                .builder()
                .board(boardDetail.orElse(null))
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
     **/
    public void deleteFreeBoardComment(Integer replyId,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = commentRepository
                .findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        if(!member.getUserId().equals(comment.getReplyWriter())){
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
    public Integer createPlaceComment(Integer placeId, CommentRequest dto, Member member) throws Exception {

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Place> detail = Optional.ofNullable(placeRepository
                .findById(placeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        Comment comment = Comment
                .builder()
                .place(detail.orElseThrow())
                .replyWriter(member.getUserId())
                .replyContents(dto.replyContents())
                .replyPoint(dto.replyPoint())
                .likeCount(0)
                .member(member)
                .build();
        //댓글 저장
        commentRepository.save(comment);
        //평점 계산 및 저장
        double avgRating = calculateAverageComment(placeId);
        System.out.println("Calculated average rating for store " + placeId + " is " + avgRating);
        detail.get().updateReviewRate(avgRating);
        redisService.saveRating(placeId.toString(),comment.getReplyPoint());
        return comment.getId();
    }

    /**
     * 가게 댓글 삭제
     * @param replyId 가게 댓글번호
     * @param placeId 가게 번호
     * @param member  로그인 인증에 필요한 객체
     * @throws CustomExceptionHandler 조회할 가게가 없는 경우,로그인이 인증 안되는 경우,댓글 작성자와 로그인한 회원이 일치하지 않은 경우(NOT_AUTH)
     * @author 양경빈
     * tRepository#findById(Object) 가게댓글을 조회하는 메서드
     * @see CommentRepository#deleteById(Object) 가게댓글을 삭제하는 메서드
     **/
    public void deletePlaceComment(Integer replyId,Integer placeId ,Member member) throws Exception {

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Comment comment = commentRepository
                .findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));

        if(!member.getUserId().equals(comment.getReplyWriter())){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        //댓글 삭제
        commentRepository.deleteById(replyId);
        // 평점 삭제
        redisService.deleteRating(placeId.toString());
        // 평점 계산 및 저장
        double avgRating = calculateAverageComment(placeId);
        comment.getPlace().updateReviewRate(avgRating);
        System.out.println("Calculated average rating for store " + placeId + " is " + avgRating);
        redisService.saveRating(placeId.toString(), avgRating);
    }

    /**
     * 최근에 작성한 댓글(limit5)
     * @author 양경빈
     * @return List<placeCommentResponseDto>
     **/
    @Transactional(readOnly = true)
    public List<placeCommentResponseDto>recentCommentTop5(){
        return commentRepository.findTop5ByOrderByCreatedTimeDesc();
    }


    /**
     * 가게 댓글 평점 계산
     * @param placeId 가게 번호
     **/
    private Double calculateAverageComment(Integer placeId) throws Exception {

        List<placeCommentResponseDto>commentResponseList = placeCommentList(placeId);

        if(commentResponseList!=null && !commentResponseList.isEmpty()){
            double totalRating = 0.0;

            for (placeCommentResponseDto commentResponse : commentResponseList) {
                totalRating += commentResponse.getReviewPoint();
            }

            double averageRating = totalRating / commentResponseList.size();

            return Math.round(averageRating * 10) / 10.0;
        }
        return 0.0;
    }
}
