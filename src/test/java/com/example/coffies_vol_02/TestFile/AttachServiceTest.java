package com.example.coffies_vol_02.TestFile;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AttachServiceTest {

    @InjectMocks
    private AttachService attachService;

    @Mock
    private AttachRepository attachRepository;

    Board board;

    NoticeBoard notice;

    Member member;

    Attach attach;

    List<AttachDto> detailfileList = new ArrayList<>();

    List<Attach>filelist = new ArrayList<>();

    @BeforeEach
    public void init(){
        member=memberDto();
        board = board();
        notice = noticeBoard();
        attach = attach();
        filelist.add(attach);
        detailfileList.add(attachDto());
        detailfileList.add(noticeDto());
    }

    @Test
    @DisplayName("파일 전체 목록(자유게시판)")
    public void BoardFileListTest()throws Exception{
        List<Attach>list= new ArrayList<>();
        list.add(attach());
        List<AttachDto>result = new ArrayList<>();
        result.add(attachDto());

        given(attachRepository.findAttachBoard(any())).willReturn(filelist);

        when(attachService.boardfilelist(any())).thenReturn(detailfileList);
        when(attachRepository.findAttachBoard(any())).thenReturn(filelist);

        result = attachService.boardfilelist(anyInt());

        verify(attachRepository).findAttachBoard(any());
    }

    @Test
    @DisplayName("파일 전체 목록(공지게시판)")
    public void NoticeFileListTest()throws Exception{
        List<Attach>list= new ArrayList<>();
        list.add(attach());
        List<AttachDto>result = new ArrayList<>();
        result.add(noticeDto());

        given(attachRepository.findAttachNoticeBoard(any())).willReturn(filelist);

        when(attachService.noticefilelist(any())).thenReturn(result);
        when(attachRepository.findAttachNoticeBoard(any())).thenReturn(filelist);

        result = attachService.noticefilelist(anyInt());

        verify(attachRepository).findAttachNoticeBoard(any());
    }

    @Test
    @DisplayName("파일 삭제(자유게시판)")
    public void BoardFileDeleteTest()throws Exception{
        given(attachRepository.findAttachBoard(board.getId())).willReturn(filelist);

        doNothing().when(attachRepository).delete(attach);
        attachService.deleteBoardAttach(board.getId());

        verify(attachRepository).delete(attach);
    }

    @Test
    @DisplayName("파일 삭제(공지게시판)")
    public void NoticeFileDeleteTest()throws Exception{
        given(attachRepository.findAttachNoticeBoard(notice.getId())).willReturn(filelist);

        doNothing().when(attachRepository).delete(attach);
        attachService.deleteNoticeAttach(notice.getId());

        verify(attachRepository).delete(attach);
    }

    @Test
    @DisplayName("파일 조회(자유게시판)")
    public void BoardFileTest(){
        given(attachRepository.findAttachByOriginFileName(attach.getOriginFileName())).willReturn(Optional.ofNullable(attach));

        AttachDto attachDto = attachService.getFreeBoardFile(attach.getOriginFileName());

        verify(attachRepository).findAttachByOriginFileName(attach.getOriginFileName());
        assertThat(attachDto).isNotNull();
    }

    @Test
    @DisplayName("파일 조회(공지게시판)")
    public void NoticeFileTest(){
        given(attachRepository.findAttachByOriginFileName(noticeDto().getOriginFileName())).willReturn(Optional.of(attach));
        AttachDto attachDto = attachService.getNoticeBoardFile(noticeDto().getOriginFileName());
        verify(attachRepository).findAttachByOriginFileName(noticeDto().getOriginFileName());
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
    private NoticeBoard noticeBoard(){
        return NoticeBoard
                .builder()
                .id(1)
                .noticeWriter(memberDto().getUserId())
                .noticeTitle("title")
                .noticeGroup("공지게시판")
                .noticeContents("내용")
                .isFixed('Y')
                .fileGroupId("notice_few3432")
                .build();
    }
    private Attach attach(){
        return Attach
                .builder()
                .id(1)
                .board(board)
                .noticeBoard(notice)
                .originFileName("c.jpg")
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .fileSize(30277L)
                .build();
    }
    private AttachDto attachDto(){
        return AttachDto
                .builder()
                .boardId(board.getId())
                .originFileName("c.jpg")
                .fileSize(30277L)
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .build();
    }

    private AttachDto noticeDto(){
        return AttachDto.builder()
                .noticeId(notice.getId())
                .originFileName("c.jpg")
                .fileSize(30277L)
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .build();
    }
}
