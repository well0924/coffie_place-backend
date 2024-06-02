package com.example.coffies_vol_02.testBoard;

import com.example.coffies_vol_02.factory.BoardFactory;
import com.example.coffies_vol_02.factory.FileFactory;
import com.example.coffies_vol_02.factory.MemberFactory;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    Member member;

    Board board;

    Attach attach;

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
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        attach = FileFactory.attach();
        boardRequestDto = BoardFactory.boardRequestDto();
        boardResponseDto = BoardFactory.boardResponse();
        attach = FileFactory.attach();
        filelist.add(attach);
        detailfileList.add(FileFactory.attachDto());
        detailfileList = attachService.boardfilelist(board.getId());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("자유게시판 목록화면")
    public void freeBoardListPage()throws Exception{

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        given(boardService.listFreeBoard(any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/page/board/list")
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("boardList"))
                .andExpect(view().name("board/boardList"))
                .andDo(print());
    }

    @Test
    @DisplayName("자유게시판 조회화면")
    public void freeBoardDetailPage()throws Exception{

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);

        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));

        given(boardService.findFreeBoard(board.getId())).willReturn(boardResponseDto);

        //이전글/다음글
        mvc.perform(
                get("/page/board/detail/{board-id}",board.getId())
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("detail"))
                .andExpect(model().attributeExists("file"))
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

        given(boardService.findFreeBoard(board.getId())).willReturn(boardResponseDto);

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

        given(boardService.findFreeBoard(board.getId())).willReturn(boardResponseDto);

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

}
