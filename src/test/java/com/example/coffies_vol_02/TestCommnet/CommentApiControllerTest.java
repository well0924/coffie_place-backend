package com.example.coffies_vol_02.TestCommnet;

import com.example.coffies_vol_02.Factory.BoardFactory;
import com.example.coffies_vol_02.Factory.CommentFactory;
import com.example.coffies_vol_02.Factory.MemberFactory;
import com.example.coffies_vol_02.Factory.PlaceFactory;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.config.constant.Role;
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
import java.util.Date;
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
    private List<placeCommentResponseDto> commentResponseDtoList = new ArrayList<>();
    List<Comment>commentList = new ArrayList<>();
    CommentRequest commentRequest;
    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        place = PlaceFactory.place();
        comment = CommentFactory.comment();
        commentRequest = CommentFactory.RequestDto();
        commentList.add(comment);
        commentResponseDtoList.add(CommentFactory.placeCommentResponseDto());
        memberRepository.save(member);
        commentRepository.save(CommentFactory.comment());
        placeRepository.findById(PlaceFactory.place().getId());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("게시판 댓글 목록")
    public void boardCommentList()throws Exception{

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));

        when(commentService.replyList(board.getId())).thenReturn(any());

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

        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(commentService.commentCreate(board.getId(),member,commentRequest)).willReturn(comment.getId());

        when(commentService.commentCreate(board.getId(),member,commentRequest)).thenReturn(comment.getId());

        mvc.perform(post("/api/comment/write/{board_id}",board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).commentCreate(anyInt(),any(),any());
    }

    @Test
    @DisplayName("게시판 댓글 삭제")
    public void boardCommentDelete()throws Exception{

        mvc.perform(delete("/api/comment/delete/{reply_id}",comment.getId())
                .with(user(customUserDetails))
                        .contentType(MediaType.ALL)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print());

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

        verify(commentService).placeCommentList(place.getId());
    }

    @Test
    @DisplayName("가게 댓글 작성")
    public void placeCommentWrite()throws Exception{

        given(commentService.placeCommentCreate(place.getId(),commentRequest, member)).willReturn(comment.getId());

        when(commentService.placeCommentCreate(place.getId(),commentRequest,member)).thenReturn(comment.getId());

        mvc.perform(post("/api/comment/place/write/{place_id}",place.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(commentRequest))
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(commentService).placeCommentCreate(any(),any(),any());
    }

    @Test
    @DisplayName("가게 댓글 삭제")
    public void placeCommentDeleteTest()throws Exception{

        mvc.perform(delete("/api/comment/place/delete/{place_id}/{reply_id}",place.getId(),comment.getId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

    }

}
