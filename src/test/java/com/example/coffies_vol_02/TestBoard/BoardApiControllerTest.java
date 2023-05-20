package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequestDto;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.util.FileHandler;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Member member;

    private Board board;

    BoardRequestDto boardRequestDto;

    BoardResponseDto boardResponseDto;

    Attach attach;

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
        boardRequestDto = getBoardRequestDto();
        boardResponseDto = boardResponseDto();
        attach = attach();
        filelist.add(attach);
        filelist = fileHandler.parseFileInfo(getBoardRequestDto().getFiles());
        detailfileList.add(attachDto());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("게시글 목록")
    public void boardList()throws Exception{

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<BoardResponseDto>responseDtoList = new ArrayList<>();
        responseDtoList.add(boardResponseDto);
        Page<BoardResponseDto>list = new PageImpl<>(responseDtoList,pageRequest,1);
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
        List<BoardResponseDto>list = new ArrayList<>();
        list.add(boardResponseDto);
        Page<BoardResponseDto>searchList = new PageImpl<>(list,pageable,1);
        given(boardService.boardSearchAll(keyword,pageable)).willReturn(searchList);

        mvc.perform(get("/api/board/search")
                .param("keyword",keyword)
                .with(user(customUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).boardSearchAll(any(),any());
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

        when(boardService.boardCreate(boardRequestDto,customUserDetails.getMember())).thenReturn(board.getId());

        mvc.perform(multipart("/api/board/write")
                        .file("files",boardRequestDto.getFiles().get(0).getBytes())
                        .file("files",boardRequestDto.getFiles().get(1).getBytes())
                        .param("boardAuthor",boardRequestDto.getBoardAuthor())
                        .param("boardTitle",boardRequestDto.getBoardTitle())
                        .param("boardContents",boardRequestDto.getBoardContents())
                        .param("passWd",boardRequestDto.getPassWd())
                        .param("readCount",String.valueOf(boardRequestDto.getReadCount()))
                        .param("fileGroupId",boardRequestDto.getFileGroupId())
                        .with(user(customUserDetails))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("POST");
                            return requestPostProcessor;})
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(boardService).boardCreate(any(),any());
        assertThat(boardRequestDto.getFiles().size()).isEqualTo(2);
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

        MockMultipartFile updateFile = new MockMultipartFile("test4", "test4.PNG", MediaType.IMAGE_PNG_VALUE, "test4".getBytes());
        boardRequestDto.setFiles(List.of(updateFile));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(fileHandler.parseFileInfo(boardRequestDto.getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);

        mvc.perform(multipart("/api/board/update/{board_id}",board.getId())
                        .file("files",boardRequestDto.getFiles().get(0).getBytes())
                        .param("boardAuthor",boardRequestDto.getBoardAuthor())
                        .param("boardTitle","update Title")
                        .param("boardContents","update contents")
                        .param("passWd","12345")
                        .param("readCount",String.valueOf(boardRequestDto.getReadCount()))
                        .param("fileGroupId",boardRequestDto.getFileGroupId())
                        .with(user(customUserDetails))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("PUT");
                            return requestPostProcessor;})
                        .contentType(MediaType.ALL)
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
        given(boardRepository.findByPassWdAndId(boardResponseDto.getPassWd(),boardResponseDto.getId())).willReturn(boardResponseDto);
        given(boardService.passwordCheck(boardResponseDto.getPassWd(),boardResponseDto.getId(),customUserDetails.getMember())).willReturn(boardResponseDto);

        when(boardService.passwordCheck(boardResponseDto.getPassWd(),boardResponseDto.getId(),customUserDetails.getMember())).thenReturn(boardResponseDto);
        mvc.perform(get("/api/board/password/{board_id}/{password}",boardResponseDto.getId(),boardResponseDto.getPassWd())
                .with(user(customUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(boardService).passwordCheck(board.getPassWd(),board.getId(),customUserDetails.getMember());
    }

    private Board board(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(member.getUserId())
                .boardTitle("test")
                .boardContents("test!")
                .passWd("132v")
                .readCount(1)
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

    private BoardRequestDto getBoardRequestDto(){
        return BoardRequestDto
                .builder()
                .boardAuthor(member.getUserId())
                .boardContents("test!")
                .boardTitle("test title")
                .fileGroupId("free_teger")
                .passWd("1234")
                .readCount(0)
                .files(List.of(
                        new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                        new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes())
                ))
                .build();
    }
    private BoardResponseDto boardResponseDto(){
        return BoardResponseDto.builder()
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
