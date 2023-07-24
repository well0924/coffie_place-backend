package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.Factory.BoardFactory;
import com.example.coffies_vol_02.Factory.FileFactory;
import com.example.coffies_vol_02.Factory.MemberFactory;
import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.MultipartFile;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BoardService boardService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private FileHandler fileHandler;

    @Mock
    private AttachRepository attachRepository;

    Member member;

    Board board;

    SearchType searchType;

    BoardRequest boardRequestDto;

    BoardResponse boardResponseDto;

    Attach attach;

    List<AttachDto>detailfileList = new ArrayList<>();

    List<Attach>filelist = new ArrayList<>();
    //첨부 파일
    List<MultipartFile>files = new ArrayList<>(List.of(
            new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
            new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
            new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())));

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
        boardRequestDto = BoardFactory.boardRequestDto();
        boardResponseDto = BoardFactory.boardResponse();
        attach = FileFactory.attach();
        filelist.add(attach);
        filelist = fileHandler.parseFileInfo(files);
        detailfileList.add(FileFactory.attachDto());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("게시글 목록")
    public void boardList()throws Exception{

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<BoardResponse>responseDtoList = new ArrayList<>();
        responseDtoList.add(boardResponseDto);
        Page<BoardResponse>list = new PageImpl<>(responseDtoList,pageRequest,1);
        given(boardRepository.boardList(pageRequest)).willReturn(list);

        when(boardService.boardAllList(pageRequest)).thenReturn(list);

        mvc.perform(get("/api/board/list")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].boardTitle").value("test"))
                .andDo(print());

        verify(boardService,atLeastOnce()).boardAllList(any(Pageable.class));
    }

    @Test
    @DisplayName("게시물 검색")
    public void boardSearchTest()throws Exception{
        String keyword = "well4149";
        Pageable pageable = PageRequest.of(1,5,Sort.by("id").descending());
        List<BoardResponse>list = new ArrayList<>();
        list.add(boardResponseDto);
        Page<BoardResponse>searchList = new PageImpl<>(list,pageable,1);
        given(boardService.boardSearchAll(searchType,keyword,pageable)).willReturn(searchList);

        mvc.perform(get("/api/board/search")
                .param("searchVal",keyword)
                        .param("searchType","w")
                .with(user(customUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).boardSearchAll(any(),any(),any());
    }

    @Test
    @DisplayName("게시글 단일 조회")
    public void boardDetailTest()throws Exception{
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));

        when(boardService.findBoard(board.getId())).thenReturn(boardResponseDto);

        mvc.perform(get("/api/board/detail/{id}",board.getId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).findBoard(board.getId());
    }

    @Test
    @DisplayName("게시글 작성")
    public void boardWriteTest()throws Exception{
        String content= objectMapper.writeValueAsString(boardRequestDto);
        MockMultipartFile file3 = new MockMultipartFile("boardDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(boardService.boardCreate(boardRequestDto,files,customUserDetails.getMember())).thenReturn(board.getId());

        mvc.perform(multipart("/api/board/write")
                        .file("files",files.get(0).getBytes())
                        .file("files",files.get(1).getBytes())
                        .file(file3)
                        .with(user(customUserDetails))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("POST");
                            return requestPostProcessor;})
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(boardService).boardCreate(any(),any(),any());
    }

    @Test
    @DisplayName("자유게시판 삭제")
    public void boardDeleteTest()throws Exception{

        mvc.perform(delete("/api/board/delete/{board_id}",board.getId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).BoardDelete(board.getId(),customUserDetails.getMember());
    }

    @Test
    @DisplayName("자유게시판 수정")
    public void boardUpdateTest()throws Exception{
        String content= objectMapper.writeValueAsString(new BoardRequest("update titlte","update contents",member.getUserId(),0,"1234","free_23bk4322"));
        MockMultipartFile file3 = new MockMultipartFile("updateDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        files.remove(0);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(fileHandler.parseFileInfo(files)).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);

        when(boardService.BoardUpdate(board.getId(),boardRequestDto,member,files)).thenReturn(board.getId());

        mvc.perform(multipart("/api/board/update/{board_id}",board.getId())
                        .file("files",files.get(0).getBytes())
                        .file(file3)
                        .with(user(customUserDetails))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("PUT");
                            return requestPostProcessor;})
                        .contentType(MediaType.ALL)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).BoardUpdate(anyInt(),any(),any(),any());
    }

    @Test
    @DisplayName("비밀번호 확인")
    public void passwordTest()throws Exception{
        given(memberRepository.findById(customUserDetails.getMember().getId())).willReturn(Optional.of(customUserDetails.getMember()));
        given(boardRepository.findByPassWdAndId(boardResponseDto.passWd(),boardResponseDto.id())).willReturn(boardResponseDto);
        given(boardService.passwordCheck(boardResponseDto.passWd(),boardResponseDto.id(),customUserDetails.getMember())).willReturn(boardResponseDto);

        when(boardService.passwordCheck(boardResponseDto.passWd(),boardResponseDto.id(),customUserDetails.getMember())).thenReturn(boardResponseDto);
        mvc.perform(get("/api/board/password/{board_id}/{password}",boardResponseDto.id(),boardResponseDto.passWd())
                .with(user(customUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).passwordCheck(board.getPassWd(),board.getId(),customUserDetails.getMember());
    }

}
