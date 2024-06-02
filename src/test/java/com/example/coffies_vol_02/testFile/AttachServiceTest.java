package com.example.coffies_vol_02.testFile;

import com.example.coffies_vol_02.factory.BoardFactory;
import com.example.coffies_vol_02.factory.FileFactory;
import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.factory.NoticeFactory;
import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.member.domain.Member;
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
        member= MemberFactory.memberDto();
        board = BoardFactory.board();
        notice = NoticeFactory.noticeBoard();
        attach = FileFactory.attach();
        filelist.add(attach);
        detailfileList.add(FileFactory.attachDto());
        detailfileList.add(FileFactory.noticeDto());
    }

    @Test
    @DisplayName("파일 전체 목록(자유게시판)")
    public void BoardFileListTest()throws Exception{
        List<Attach>list= new ArrayList<>();
        list.add(attach);
        List<AttachDto>result = new ArrayList<>();
        result.add(FileFactory.attachDto());

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
        list.add(attach);
        List<AttachDto>result = new ArrayList<>();
        result.add(FileFactory.noticeDto());

        given(attachRepository.findAttachNoticeBoard(notice.getId())).willReturn(filelist);

        when(attachService.noticefilelist(notice.getId())).thenReturn(result);
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
        given(attachRepository.findAttachByOriginFileName(FileFactory.noticeDto().getOriginFileName())).willReturn(Optional.of(attach));
        AttachDto attachDto = attachService.getNoticeBoardFile(FileFactory.noticeDto().getOriginFileName());
        verify(attachRepository).findAttachByOriginFileName(FileFactory.noticeDto().getOriginFileName());
    }

}
