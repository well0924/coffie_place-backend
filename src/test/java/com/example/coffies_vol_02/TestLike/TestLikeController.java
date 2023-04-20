package com.example.coffies_vol_02.TestLike;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Like.domain.CommentLike;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Like.repository.CommentLikeRepository;
import com.example.coffies_vol_02.Like.repository.LikeRepository;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PlaceRepository placeRepository;
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
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        board = getBoard();
        place = getPlace();
        like = getLike();
        comment = comment();
        commentLike = getCommentLike();
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

        mvc.perform(post("/api/like/plus/{board_id}",getBoard().getId())
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

        mvc.perform(delete("/api/like/minus/{board_id}",getBoard().getId())
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
                        get("/api/like/comment/{reply_id}",comment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요+1")
    public void commentLikePlusTest()throws Exception{

        mvc.perform(post("/api/like/plus/{place_id}/{reply_id}",1,7)
                        .content(objectMapper.writeValueAsString(1))
                        .content(objectMapper.writeValueAsString(7))
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요-1")
    public void commentLikeMinusTest() throws Exception {
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));
        given(commentLikeRepository.save(commentLike)).willReturn(commentLike);

        mvc.perform(delete("/api/like/minus/{place_id}/{reply_id}",1,7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password(bCryptPasswordEncoder.encode("qwer4149!!"))
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
    private Board getBoard(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(memberDto().getUserId())
                .boardTitle("test")
                .boardContents("test!!")
                .fileGroupId("free_sve345s")
                .readCount(0)
                .passWd("1234")
                .member(memberDto())
                .build();
    }
    private Comment comment(){
        return Comment
                .builder()
                .id(5)
                .replyContents("reply test")
                .replyWriter(member.getUserId())
                .replyPoint(3)
                .board(board)
                .member(member)
                .build();
    }
    private Like getLike(){
        return Like
                .builder()
                .board(board)
                .member(member)
                .comment(comment)
                .build();
    }
    private Place getPlace(){
        return Place
                .builder()
                .id(1)
                .placeName("we")
                .placeStart("10:00")
                .placeClose("20:00")
                .placePhone("010-2345-5666")
                .placeAuthor(member.getUserId())
                .fileGroupId("place_sdc353")
                .reviewRate(0.0)
                .placeLat(123.34)
                .placeLng(23.35)
                .placeAddr1("sssss-ssss-ss")
                .placeAddr2("sdcsvefv")
                .build();
    }
    private CommentLike getCommentLike(){
        return CommentLike
                .builder()
                .member(member)
                .comment(comment)
                .build();
    }
}
