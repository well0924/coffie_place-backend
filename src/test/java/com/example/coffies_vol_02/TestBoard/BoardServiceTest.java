package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequestDto;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AttachRepository attachRepository;

    @Mock
    private AttachService attachService;

    @Mock
    private FileHandler fileHandler;

    Member member;

    MemberDto.MemberResponseDto memberResponseDto;

    Board board;

    BoardRequestDto boardRequestDto;

    BoardResponseDto boardResponseDto;

    Attach attach;

    List<AttachDto>detailfileList = new ArrayList<>();

    List<Attach>filelist = new ArrayList<>();

    @BeforeEach
    public void init() throws Exception {
        member = memberDto();
        memberResponseDto = responseDto();
        board = board();
        boardRequestDto = getBoardRequestDto();
        boardResponseDto = boardResponseDto();
        attach = attach();
        filelist.add(attach);
        filelist = fileHandler.parseFileInfo(getBoardRequestDto().getFiles());
        detailfileList.add(attachDto());
        detailfileList = attachService.boardfilelist(board.getId());
    }

    @Test
    @DisplayName("게시글 목록")
    public void boardList(){

        //given
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        given(boardRepository.boardList(pageRequest)).willReturn(Page.empty());

        //when
        Page<BoardResponseDto>result = boardService.boardAllList(pageRequest);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("게시글 단일 조회")
    public void boardDetail(){
        //given
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));

        //when
        BoardResponseDto result = boardService.findBoard(board.getId());

        //then
        assertThat(result.getBoardAuthor()).isEqualTo(board.getBoardAuthor());
    }

    @Test
    @DisplayName("게시글 단일 조회실패")
    public void boardDetailFail(){
        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()->{
            BoardResponseDto result = boardService.findBoard(0);
        });

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.BOARD_NOT_FOUND);
    }

    @Test
    @DisplayName("게시물 검색-작성자")
    public void boardSearchTest(){
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<BoardResponseDto>list = new ArrayList<>();
        list.add(boardResponseDto);

        Page<BoardResponseDto> result = new PageImpl<>(list,pageRequest,1);

        //작성자
        String keyword = "well4149";
        given(boardRepository.findAllSearch(keyword,pageRequest)).willReturn(result);

        when(boardService.boardSearchAll(keyword,pageRequest)).thenReturn(result);
        result = boardService.boardSearchAll(keyword,pageRequest);

        assertThat(keyword).isEqualTo(result.stream().toList().get(0).getBoardAuthor());
    }

    @Test
    @DisplayName("게시글 작성")
    public void boardWrite() throws Exception {
        //given
        init();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.save(any())).willReturn(board);
        given(fileHandler.parseFileInfo(boardRequestDto.getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);

        boardService.boardCreate(boardRequestDto,member);

        verify(boardRepository).save(any());
        verify(fileHandler,times(3)).parseFileInfo(any());
    }

    @Test
    @DisplayName("게시글 작성-단순 글 작성")
    public void boardJustWrite() throws Exception {
        //given
        init();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.save(any())).willReturn(board);
        boardRequestDto.setFiles(null);
        boardService.boardCreate(boardRequestDto,member);

        verify(boardRepository).save(any());
    }

    @Test
    @DisplayName("게시글 작성-실패(로그인 안한 경우)")
    public void boardWriteFail1(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.boardCreate(boardRequestDto,null));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("게시글 삭제")
    public void boardDelete() throws Exception {
        board = board();

        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(fileHandler.parseFileInfo(boardRequestDto.getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);

        attachService.boardfilelist(board.getId());
        boardService.BoardDelete(board.getId(),member);

        verify(boardRepository).deleteById(any());
        verify(fileHandler,times(1)).parseFileInfo(any());
    }

    @Test
    @DisplayName("게시글 삭제 실패- 로그인을 안한경우")
    public void boardDeleteFail2() throws Exception {
        init();
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.BoardDelete(board.getId(),null));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }
    @Test
    @DisplayName("게시글 삭제- 작성자의 이름이 다른경우")
    public void boardDeleteFail3(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String differentBoardAuthor = "bbbb";

        member.setUserId(differentBoardAuthor);

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.BoardDelete(board.getId(),member));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.NOT_AUTH);
    }

    @Test
    @DisplayName("게시글 수정")
    public void boardUpdate() throws Exception {
        //given
        init();
        MockMultipartFile updateFile = new MockMultipartFile("test4", "test4.PNG", MediaType.IMAGE_PNG_VALUE, "test4".getBytes());
        boardRequestDto.setFiles(List.of(updateFile));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(fileHandler.parseFileInfo(boardRequestDto.getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);

        //when
        boardService.BoardUpdate(board.getId(),boardRequestDto,member,boardRequestDto.getFiles());

        verify(fileHandler,atLeastOnce()).parseFileInfo(any());
    }

    @Test
    @DisplayName("게시글 수정실패 - 로그인을 하지 않은 경우")
    public void boardUpdateFail1() throws Exception {
        init();
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.BoardUpdate(board.getId(),getBoardRequestDto(),null,getBoardRequestDto().getFiles()));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("게시글 수정 - 작성자가 다른 경우")
    public void boardUpdateFail2(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String differentBoardAuthor = "bbbb";

        member.setUserId(differentBoardAuthor);

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.BoardUpdate(board.getId(),getBoardRequestDto(),member,getBoardRequestDto().getFiles()));

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.NOT_AUTH);
    }

    @Test
    @DisplayName("자유게시판 비밀번호입력-성공")
    public void passwordCheckTest(){
        //given
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.findByPassWdAndId(board.getPassWd(),board.getId())).willReturn(boardResponseDto);
        
        //when
        when(boardService.passwordCheck(board.getPassWd(),board.getId(),member)).thenReturn(boardResponseDto);
        BoardResponseDto result = boardService.passwordCheck(board.getPassWd(),board.getId(),member);
        
        //then
        assertThat(result).isEqualTo(boardResponseDto);
    }

    @Test
    @DisplayName("자유게시판 비밀번호입력-실패(로그인이 안된 경우)")
    public void passwordCheckTestFail1(){
        //given
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());
        given(boardRepository.findByPassWdAndId(board.getPassWd(),board.getId())).willReturn(boardResponseDto);

        //when
        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.passwordCheck(board.getPassWd(),board.getId(),null));

        //then
        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("자유게시판 비밀번호입력-실패(비밀번호가 일치하지 않은 경우)")
    public void passwordCheckTestFail2(){
        //given
        String wrongPassword = "wqve1234";
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> boardService.passwordCheck(wrongPassword,board.getId(),member));

        //then
        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.NOT_MATCH_PASSWORD);
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
    private BoardRequestDto getBoardRequestDto(){
        return BoardRequestDto
                .builder()
                .boardAuthor(member.getUserId())
                .boardContents("test!")
                .boardTitle("test title")
                .fileGroupId("free_teger")
                .passWd("1234")
                .readCount(0)
                .files(
                        List.of(
                        new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                        new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                        new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes()))
                )
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
