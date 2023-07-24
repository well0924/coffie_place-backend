package com.example.coffies_vol_02.TestCommnet;

import com.example.coffies_vol_02.Factory.BoardFactory;
import com.example.coffies_vol_02.Factory.CommentFactory;
import com.example.coffies_vol_02.Factory.MemberFactory;
import com.example.coffies_vol_02.Factory.PlaceFactory;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private MemberRepository memberRepository;

    Member member;

    Board board;

    Place place;

    Comment comment;

    MemberResponse responseDto;

    BoardResponse boardResponseDto;

    CommentRequest commentRequestDto;

    placeCommentResponseDto placeCommentResponseDto;

    @BeforeEach
    public void init(){
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        place = PlaceFactory.place();
        comment = CommentFactory.comment();
        responseDto = MemberFactory.response();
        commentRequestDto = CommentFactory.RequestDto();
        placeCommentResponseDto = CommentFactory.placeCommentResponseDto();
        boardResponseDto = BoardFactory.boardResponse();
    }

    @DisplayName("댓글 목록-성공")
    @Test
    public void CommentListTest() throws Exception {
        List<placeCommentResponseDto>result = new ArrayList<>();
        List<Comment>list = new ArrayList<>();
        list.add(comment);
        result.add(placeCommentResponseDto);

        given(commentRepository.findByBoardId(board.getId())).willReturn(list);
        given(commentService.replyList(board.getId())).willReturn(anyList());

        when(commentRepository.findByBoardId(board.getId())).thenReturn(list);
        when(commentService.replyList(board.getId())).thenReturn(anyList());
        result = commentService.replyList(board.getId());

        verify(commentRepository).findByBoardId(board.getId());
    }

    @Test
    @DisplayName("댓글 작성-성공")
    public void CommentWriteTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.save(comment)).willReturn(comment);
        given(commentService.commentCreate(board.getId(),member,commentRequestDto)).willReturn(any());

        when(commentService.commentCreate(board.getId(),member,commentRequestDto)).thenReturn(anyInt());
        commentService.commentCreate(board.getId(),member,commentRequestDto);

        verify(commentRepository).save(any());
    }

    @Test
    @DisplayName("게시판 댓글작성 실패-로그인이 안된 경우")
    public void CommentWriteFail(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.empty());
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));

        member= null;

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> commentService.commentCreate(board.getId(),null,commentRequestDto));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("댓글 삭제-성공")
    public void CommentDeleteTest(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        doNothing().when(commentRepository).deleteById(comment.getId());
        commentService.commentDelete(comment.getId(),member);

        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    @DisplayName("댓글 삭제실패-로그인이 안 된 경우")
    public void CommentDeleteFail1(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        assertThatThrownBy(()->commentService.commentDelete(board.getId(),null))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("댓글 삭제실패-작성자가 다른 경우")
    public void CommentDeleteFail2(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        String userid = "well";
        member.setUserId(userid);

        assertThatThrownBy(()->commentService.commentDelete(anyInt(),member))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("가게 댓글목록")
    public void PlaceCommentListTest() throws Exception {
       List<Comment>commentList = new ArrayList<>();
       commentList.add(comment);
       List<placeCommentResponseDto>list = new ArrayList<>();
       list.add(placeCommentResponseDto);

       given(commentRepository.findByPlaceId(place.getId())).willReturn(commentList);
       given(commentService.placeCommentList(place.getId())).willReturn(anyList());

       when(commentService.placeCommentList(place.getId())).thenReturn(anyList());

       commentService.placeCommentList(place.getId());

       verify(commentRepository).findByPlaceId(any());
    }

    @Test
    @DisplayName("가게 댓글 작성")
    public void PlaceCommentCreateTest(){
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));

        given(commentService.placeCommentCreate(place.getId(),commentRequestDto,member)).willReturn(anyInt());
        when(commentService.placeCommentCreate(place.getId(),commentRequestDto,member)).thenReturn(anyInt());
        then(commentService.placeCommentCreate(place.getId(),commentRequestDto,member));
    }

    @Test
    @DisplayName("가게 댓글 작성-실패(로그인을 안한 경우)")
    public void PlaceCommentCreateTestFail1(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());
        member = null;

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> commentService.placeCommentCreate(place.getId(),commentRequestDto,null));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }
    
    @Test
    @DisplayName("가게 댓글 삭제")
    public void PlaceCommentDeleteTest(){
        //given
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        //when
        doNothing().when(commentRepository).deleteById(comment.getId());
        commentService.placeCommentDelete(comment.getId(),member);
        //then
        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    @DisplayName("가게 댓글 삭제-실패(로그인을 하지 않은경우)")
    public void PlaceCommentDeleteTestFail1(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> commentService.placeCommentDelete(place.getId(),null));
        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("가게 댓글 삭제-실패(작성자가 다른경우)")
    public void PlaceCommentDeleteTestFail2(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(place.getId())).willReturn(Optional.of(comment));

        String userId = "we";
        member.setUserId(userId);

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> commentService.placeCommentDelete(place.getId(),member));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.NOT_AUTH);
    }

    @Test
    @DisplayName("댓글 평점계산")
    public void replyPointTest() throws Exception {
        given(commentRepository.getStarAvgByPlaceId(place.getId())).willReturn(place.getReviewRate());

        when(commentService.getStarAvgByPlaceId(place.getId())).thenReturn(place.getReviewRate());
        Double starAvg = commentService.getStarAvgByPlaceId(place.getId());

        verify(commentRepository,atLeastOnce()).getStarAvgByPlaceId(place.getId());
    }

    @Test
    @DisplayName("가게 평점 출력")
    public void cafeReviewRateTest() throws Exception {
        given(commentRepository.getStarAvgByPlaceId(place.getId())).willReturn(place.getReviewRate());

        doNothing().when(commentRepository).cafeReviewRate(place.getReviewRate(),place.getId());
        commentService.cafeReviewRate(place.getReviewRate(),place.getId());

        verify(commentRepository).cafeReviewRate(anyDouble(),anyInt());
    }

    @Test
    @DisplayName("가게 댓글 평점 출력")
    public void updateStarTest() throws Exception {
        given(commentRepository.getStarAvgByPlaceId(place.getId())).willReturn(place.getReviewRate());

        doNothing().when(commentRepository).cafeReviewRate(place.getReviewRate(),place.getId());
        commentService.cafeReviewRate(place.getReviewRate(),place.getId());
        commentService.updateStar(place.getId());

    }

}
