package com.example.coffies_vol_02.TestCommnet;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.commentRequestDto;
import com.example.coffies_vol_02.commnet.domain.dto.response.commentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentApiControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    Comment comment;
    Board board;
    Member member;
    Place place;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PlaceRepository placeRepository;
    @MockBean
    private CommentService commentService;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();
    private CustomUserDetails customUserDetails;
    private List<commentResponseDto> commentResponseDtoList = new ArrayList<>();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        board = board();
        place = place();
        comment = comment();
        commentResponseDtoList.add(commentResponseDto());
        memberRepository.save(member);
        commentRepository.save(comment());
        placeRepository.findById(place().getId());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("게시판 댓글 목록")
    public void boardCommentList()throws Exception{

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));

        when(commentService.replyList(board().getId())).thenReturn(any());

        mvc.perform(get("/api/comment/list/{board_id}",board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).replyList(board.getId());
    }

    @Test
    @DisplayName("게시판 댓글 작성")
    public void boardCommentWrite()throws Exception{

        commentRequestDto commentRequestDto = new commentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(commentService.commentCreate(board.getId(),member,commentRequestDto)).willReturn(comment.getId());

        when(commentService.commentCreate(board.getId(),member,commentRequestDto)).thenReturn(comment.getId());

        mvc.perform(post("/api/comment/write/{board_id}",board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).commentCreate(anyInt(),any(),any());
    }

    @Test
    @DisplayName("게시판 댓글 삭제")
    public void boardCommentDelete()throws Exception{
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board().getId())).willReturn(Optional.of(board));

        doNothing().when(commentService).commentDelete(comment().getId(),member);

        mvc.perform(delete("/api/comment/delete/{reply_id}",comment().getId())
                .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).commentDelete(anyInt(),any());
    }

    @Test
    @DisplayName("가게 댓글 목록")
    public void placeCommentListTest()throws Exception{
        given(commentRepository.findByPlaceId(place.getId())).willReturn(anyList());

        when(commentService.placeCommentList(place.getId())).thenReturn(commentResponseDtoList);

        mvc.perform(get("/api/comment/place/list/{place_id}",place.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).placeCommentList(comment().getId());
    }

    @Test
    @DisplayName("가게 댓글 작성")
    public void placeCommentWrite()throws Exception{

        commentRequestDto commentRequestDto = new commentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        given(commentService.placeCommentCreate(place().getId(), commentRequestDto, member)).willReturn(comment().getId());

        when(commentService.placeCommentCreate(place.getId(),commentRequestDto,member)).thenReturn(comment.getId());

        mvc.perform(post("/api/comment/place/write/{place_id}",place.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(commentRequestDto))
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).placeCommentCreate(any(),any(),any());
    }

    @Test
    @DisplayName("가게 댓글 삭제")
    public void placeCommentDeleteTest()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        doNothing().when(commentService).placeCommentDelete(comment.getId(),member);

        mvc.perform(delete("/api/comment/place/delete/{place_id}/{reply_id}",place.getId(),comment.getId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).placeCommentDelete(anyInt(),any());
    }

    private Comment comment(){
        return Comment
                .builder()
                .id(1)
                .replyContents("reply test")
                .replyWriter(memberDto().getUserId())
                .replyPoint(3)
                .board(board())
                .member(memberDto())
                .place(place())
                .build();
    }
    private Board board(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(memberDto().getUserId())
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

    private commentResponseDto commentResponseDto(){
        return commentResponseDto
                .builder()
                .comment(comment())
                .build();
    }

    private commentResponseDto placeCommentResponse(){
        return commentResponseDto
                .builder()
                .comment(comment())
                .build();
    }
}
