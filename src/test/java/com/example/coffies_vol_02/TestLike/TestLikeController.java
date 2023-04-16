package com.example.coffies_vol_02.TestLike;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Like.repository.LikeRepository;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestLikeController {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    Member member;
    Board board;
    Like like;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();
    private CustomUserDetails customUserDetails;

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        board = getBoard();
        like = getLike();
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

        mvc.perform(delete("/api/like/minus/{board_id}",getBoard().getId())
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
    private Like getLike(){
        return Like
                .builder()
                .board(board)
                .member(member)
                .build();
    }
}
