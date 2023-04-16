package com.example.coffies_vol_02.TestCommnet;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Commnet.service.CommentService;
import com.example.coffies_vol_02.Config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
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
    private CommentService commentService;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();
    private CustomUserDetails customUserDetails;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PlaceRepository placeRepository;

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

        mvc.perform(get("/api/comment/list/{board_id}",board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(user(customUserDetails)))
                .andDo(print());

    }

    @Test
    @DisplayName("게시판 댓글 작성")
    public void boardCommentWrite()throws Exception{

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        mvc.perform(post("/api/comment/write/{board_id}",board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("게시판 댓글 삭제")
    public void boardCommentDelete()throws Exception{
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board().getId())).willReturn(Optional.of(board));

        commentService.commentDelete(comment().getId(),member);

        mvc.perform(delete("/api/comment/delete/{reply_id}",comment().getId())
                .with(user(customUserDetails)))
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    @DisplayName("가게 댓글 목록")
    public void placeCommentListTest()throws Exception{
        mvc.perform(get("/api/comment/placelist/{place_id}",place.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("가게 댓글 작성")
    public void placeCommentWrite()throws Exception{
        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        mvc.perform(post("/api/comment/placewrite/{place_id}",place.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(commentRequestDto))
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("가게 댓글 삭제")
    @Disabled
    public void placeCommentDeleteTest()throws Exception{
        mvc.perform(delete("/api/comment/placedelete/{place_id}/{reply_id}",any(),any())
                .with(user(customUserDetails)))
                .andExpect(status().is4xxClientError())
                .andDo(print());
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
