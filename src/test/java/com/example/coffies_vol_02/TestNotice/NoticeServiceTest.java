package com.example.coffies_vol_02.TestNotice;

import com.example.coffies_vol_02.Factory.FileFactory;
import com.example.coffies_vol_02.Factory.MemberFactory;
import com.example.coffies_vol_02.Factory.NoticeFactory;
import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
import com.example.coffies_vol_02.notice.service.NoticeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NoticeServiceTest {
    @InjectMocks
    private NoticeService noticeService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private NoticeBoardRepository noticeBoardRepository;
    @Mock
    private AttachRepository attachRepository;
    @Mock
    private FileHandler fileHandler;
    @Mock
    private AttachService attachService;
    Member member;
    MemberResponse memberResponseDto;
    Attach attach;
    NoticeBoard noticeBoard;
    NoticeRequest request;
    NoticeResponse response;
    SearchType searchType;
    List<AttachDto> detailfileList = new ArrayList<>();
    List<Attach>filelist = new ArrayList<>();
    List<MultipartFile>files = new ArrayList<>(List.of(
            new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
            new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
            new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())));

    @BeforeEach
    public void init() throws Exception {
        member = MemberFactory.memberDto();
        memberResponseDto = MemberFactory.response();
        noticeBoard = NoticeFactory.noticeBoard();
        request = NoticeFactory.noticeRequest();
        response = NoticeFactory.response();
        attach = FileFactory.attach();
        filelist.add(attach);
        filelist = fileHandler.parseFileInfo(files);
        detailfileList.add(FileFactory.attachDto());
        detailfileList = attachService.boardfilelist(noticeBoard.getId());
    }
    @Test
    @DisplayName("공지게시글 목록")
    public void noticeBoardListTest(){
        PageRequest pageRequest = PageRequest.of(0,5, Sort.by("id"));
        given(noticeBoardRepository.findAllList(pageRequest)).willReturn(Page.empty());

        Page<NoticeResponse>result = noticeService.noticeAllList(pageRequest);

        Assertions.assertThat(result).isEmpty();
    }
    @Test
    @DisplayName("게시글 단일조회")
    public void noticeBoardDetailTest(){
        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));

        NoticeResponse detail = noticeService.findNotice(noticeBoard.getId());

        Assertions.assertThat(detail).isNotNull();
    }
    @Test
    @DisplayName("공지 게시글 검색")
    public void noticeBoardSearchTest(){
        String keyword = "well4149";
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<NoticeResponse>list = new ArrayList<>();
        list.add(response);
        Page<NoticeResponse>response = new PageImpl<>(list,pageRequest,1);

        given(noticeBoardRepository.findAllSearchList(searchType,keyword,pageRequest)).willReturn(response);

        when(noticeService.noticeSearchAll(searchType,keyword,pageRequest)).thenReturn(response);
        response = noticeService.noticeSearchAll(searchType,keyword,pageRequest);

        assertThat(response.toList().get(0).noticeWriter()).isEqualTo(keyword);
    }

    @Test
    @DisplayName("공지 게시글 작성")
    public void noticeBoardWriteTest() throws Exception {
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(noticeBoardRepository.save(noticeBoard)).willReturn(noticeBoard);
        given(fileHandler.parseFileInfo(files)).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);

        noticeService.noticeCreate(request,files);

        Mockito.verify(noticeBoardRepository).save(ArgumentMatchers.any());
        verify(fileHandler,times(2)).parseFileInfo(any());

    }
    @Test
    @DisplayName("게시글 수정")
    public void noticeBoardUpdateTest()throws Exception{

        MockMultipartFile updateFile = new MockMultipartFile("test4", "test4.PNG", MediaType.IMAGE_PNG_VALUE, "test4".getBytes());
        files.add(updateFile);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));
        given(fileHandler.parseFileInfo(files)).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachNoticeBoard(noticeBoard.getId())).willReturn(filelist);

        noticeService.noticeUpdate(noticeBoard.getId(),request,files);

        verify(fileHandler,atLeastOnce()).parseFileInfo(any());
    }
    @Test
    @DisplayName("게시글 삭제")
    public void noticeBoardDeleteTest()throws Exception{
        noticeBoard= NoticeFactory.noticeBoard();
        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(fileHandler.parseFileInfo(files)).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachNoticeBoard(noticeBoard.getId())).willReturn(filelist);

        attachService.noticefilelist(noticeBoard.getId());
        noticeService.noticeDelete(noticeBoard.getId());

        verify(noticeBoardRepository).deleteById(any());
        verify(fileHandler,times(1)).parseFileInfo(any());
    }
}
