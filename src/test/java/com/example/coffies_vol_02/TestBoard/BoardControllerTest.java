package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mvc;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private AttachRepository attachRepository;
    @MockBean
    private AttachService attachService;
    @MockBean
    private BoardService boardService;
    private Member member;
    private Board board;
    private Attach attach;
    BoardRequest boardRequestDto;
    BoardResponse boardResponseDto;
    List<AttachDto>detailfileList = new ArrayList<>();
    List<Attach>filelist = new ArrayList<>();
    private CustomUserDetails customUserDetails;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        board = board();
        attach = attach();
        boardRequestDto = getBoardRequestDto();
        boardResponseDto = boardResponseDto();
        attach = attach();
        filelist.add(attach);
        detailfileList.add(attachDto());
        detailfileList = attachService.boardfilelist(board.getId());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("자유게시판 목록화면")
    public void freeBoardListPage()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardService.boardAllList(any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/page/board/list")
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("board/boardList"))
                .andDo(print());
    }

    @Test
    @DisplayName("자유게시판 조회화면")
    public void freeBoardDetailPage()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(boardService.findBoard(board.getId())).willReturn(boardResponseDto());
        //이전글/다음글
        given(boardService.findPreviousBoard(board.getId())).willReturn(Optional.empty());
        given(boardService.findNextBoard(board.getId())).willReturn(Optional.empty());

        mvc.perform(
                get("/page/board/detail/{board-id}",board.getId())
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("detail"))
                .andExpect(model().attributeExists("file"))
                .andExpect(model().attributeExists("next"))
                .andExpect(model().attributeExists("previous"))
                .andExpect(view().name("board/detailBoard"))
                .andDo(print());
    }
    @Test
    @DisplayName("자유게시판 작성화면")
    public void freeBoardWritePage()throws Exception{
        mvc.perform(get("/page/board/writePage")
                        .with(user(customUserDetails))
                        .contentType(MediaType.TEXT_HTML)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("fileGroupId"))
                .andExpect(view().name("/board/writeBoard"))
                .andDo(print());
    }
    @Test
    @DisplayName("자유게시판 비밀번호 조회화면")
    public void freeBoardPasswordCheckPage()throws Exception{
        given(boardService.findBoard(board.getId())).willReturn(boardResponseDto());

        mvc.perform(get("/page/board/passwordCheck/{board_id}",board.getId())
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("pwd"))
                .andExpect(view().name("/board/passWordCheck"))
                .andDo(print());
    }

    @Test
    @DisplayName("자유게시판 수정삭제화면")
    public void freeBoardModifyPage()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(boardService.findBoard(board.getId())).willReturn(boardResponseDto());

        mvc.perform(
                        get("/page/board/modify/{board_id}",board.getId())
                                .with(user(customUserDetails))
                                .contentType(MediaType.TEXT_HTML)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("detail"))
                .andExpect(model().attributeExists("file"))
                .andExpect(view().name("/board/boardModify"))
                .andDo(print());

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
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .role(Role.ROLE_ADMIN)
                .build();
    }
    private Board board(){
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
    private BoardRequest getBoardRequestDto(){
        return new BoardRequest(
                board.getBoardTitle(),
                board.getBoardContents(),
                board.getMember().getUserId(),
                board.getReadCount(),
                board.getPassWd(),
                board.getFileGroupId(),
                List.of(
                        new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                        new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                        new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes()))
        );
    }
    private BoardResponse boardResponseDto(){
        return new BoardResponse(board);
    }

    private Attach attach(){
        return Attach
                .builder()
                .originFileName("c.jpg")
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .fileSize(30277L)
                .build();
    }
    private AttachDto attachDto(){
        return AttachDto
                .builder()
                .boardId(board.getId())
                .noticeId(1)
                .originFileName("c.jpg")
                .fileSize(30277L)
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .build();
    }
}
