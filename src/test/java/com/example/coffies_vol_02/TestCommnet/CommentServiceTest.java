package com.example.coffies_vol_02.TestCommnet;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Commnet.service.CommentService;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PlaceRepository placeRepository;

    Member member;
    Board board;
    Place place;
    Comment comment;

    MemberDto.MemberResponseDto responseDto;
    BoardDto.BoardResponseDto boardResponseDto;
    CommentDto.CommentResponseDto commentResponseDto;

    @BeforeEach
    public void init(){
        member = memberDto();
        responseDto = responseDto();
        comment = comment();
        commentResponseDto = commentResponseDto();
        boardResponseDto = boardResponseDto();
        board = board();
        place = place();
    }

    @DisplayName("댓글 목록-성공")
    @Test
    public void CommentListTest() throws Exception {
        List<Comment>list = new ArrayList<>();
        list.add(comment);
        given(commentRepository.findByBoardId(any())).willReturn(list);

        List<CommentDto.CommentResponseDto>result = commentService.replyList(board.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("댓글 작성-성공")
    public void CommentWriteTest(){
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment().getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        given(commentService.replyWrite(anyInt(),member,commentRequestDto)).willReturn(0);
        when(commentService.replyWrite(anyInt(),member,commentRequestDto)).thenReturn(0);
        then(commentService.replyWrite(anyInt(),member,commentRequestDto));
    }
    @Test
    @DisplayName("댓글 삭제-성공")
    public void CommentDeleteTest(){
        given(boardRepository.findById(board().getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        commentService.commentDelete(comment.getId(),member);
    }
    @Test
    @DisplayName("게시판 댓글작성 실패-로그인이 안된 경우")
    public void CommentWriteFail(){
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));

        member= null;

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(null);
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        assertThatThrownBy(()->commentService.replyWrite(anyInt(),member,commentRequestDto))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("댓글 삭제실패-작성자가 다른 경우")
    public void CommentDeleteFail2(){
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));

        String userid = "well";
        member.setUserId(userid);

        assertThatThrownBy(()->commentService.commentDelete(comment.getId(),member))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("가게 댓글목록")
    public void PlaceCommentListTest() throws Exception {
        List<Comment>commentList = new ArrayList<>();
        commentList.add(comment);

        given(commentRepository.findByPlaceId(anyInt())).willReturn(commentList);

        List<CommentDto.CommentResponseDto>result = commentService.placeCommentList(1);

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("가게 댓글 작성")
    public void PlaceCommentCreateTest(){
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        given(commentService.placeCommentWrite(place.getId(),commentRequestDto,member)).willReturn(anyInt());
        when(commentService.placeCommentWrite(place.getId(),commentRequestDto,member)).thenReturn(anyInt());
        then(commentService.placeCommentWrite(place.getId(),commentRequestDto,member));
    }

    @Test
    @DisplayName("가게 댓글 삭제")
    public void PlaceCommentDeleteTest(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        commentService.placeCommentDelete(comment.getId(),member);
    }

    private Comment comment(){
        return Comment
                .builder()
                .replyContents("reply test")
                .replyWriter(member.getUserId())
                .replyPoint(3)
                .board(board)
                .member(member)
                .place(place)
                .build();
    }
    private Board board(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(member.getUserId())
                .boardTitle("test")
                .boardContents("test!")
                .passWd("132v")
                .readCount(0)
                .fileGroupId("free_weft33")
                .member(member)
                .build();
    }
    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!!")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .build();
    }
    private Place place(){
        return Place
                .builder()
                .id(1)
                .placeLng(123.3443)
                .placeLat(23.34322)
                .placeAddr1("xxxx시 xx구")
                .placeAddr2("ㅁㄴㅇㄹ")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(0.0)
                .fileGroupId("place_fre353")
                .placeName("test place1")
                .build();
    }

    private MemberDto.MemberResponseDto responseDto(){
        return MemberDto.MemberResponseDto
                .builder()
                .id(1)
                .userId("well4149")
                .password(memberDto().getPassword())
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }
    private BoardDto.BoardResponseDto boardResponseDto(){
        return BoardDto.BoardResponseDto.builder()
                .id(board().getId())
                .boardTitle(board().getBoardTitle())
                .boardAuthor(board().getBoardAuthor())
                .boardContents(board().getBoardContents())
                .fileGroupId(board().getFileGroupId())
                .readCount(board().getReadCount())
                .passWd(board().getPassWd())
                .updatedTime(LocalDateTime.now())
                .createdTime(LocalDateTime.now())
                .build();
    }

    private CommentDto.CommentResponseDto commentResponseDto(){
        return CommentDto.CommentResponseDto
                .builder()
                .comment(comment())
                .build();
    }
}
