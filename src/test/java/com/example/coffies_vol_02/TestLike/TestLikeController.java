package com.example.coffies_vol_02.TestLike;

import com.example.coffies_vol_02.Factory.*;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.like.domain.Like;
import com.example.coffies_vol_02.like.repository.CommentLikeRepository;
import com.example.coffies_vol_02.like.repository.LikeRepository;
import com.example.coffies_vol_02.member.domain.Member;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class TestLikeController {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    ObjectMapper objectMapper;
    Member member;
    Board board;
    Comment comment;
    Place place;
    Like like;
    CommentLike commentLike;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();
    private CustomUserDetails customUserDetails;

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        place = PlaceFactory.place();
        like = LikeFactory.getLike();
        comment = CommentFactory.comment();
        commentLike = LikeFactory.getCommentLike();
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("게시글 좋아요 카운트")
    public void boardLikeCountTest() throws Exception {
        mvc.perform(
                get("/api/like/{board_id}",board.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 좋아요+1")
    public void boardLikePlusTest()throws Exception{

        mvc.perform(post("/api/like/plus/{board_id}",board.getId())
                        .content(objectMapper.writeValueAsString(board.getId()))
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 좋아요-1")
    public void boardLikeMinusTest() throws Exception {
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(like));
        given(likeRepository.save(like)).willReturn(like);

        mvc.perform(delete("/api/like/minus/{board_id}",board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요 카운트")
    public void commentLikeCountTest() throws Exception {
        mvc.perform(
                        get("/api/like/comment/{reply_id}",360)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요+1")
    public void commentLikePlusTest()throws Exception{

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));

        mvc.perform(post("/api/like/plus/{place_id}/{reply_id}",22,360)
                        .content(objectMapper.writeValueAsString(22))
                        .content(objectMapper.writeValueAsString(360))
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요-1")
    public void commentLikeMinusTest() throws Exception {

        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));
        given(commentLikeRepository.findByMemberAndComment(member,comment)).willReturn(Optional.of(commentLike));
        given(commentLikeRepository.save(commentLike)).willReturn(commentLike);

        mvc.perform(delete("/api/like/minus/{place_id}/{reply_id}",22,360)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

}
