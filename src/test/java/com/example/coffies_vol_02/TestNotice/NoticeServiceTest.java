package com.example.coffies_vol_02.TestNotice;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequestDto;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponseDto;
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

import java.time.LocalDateTime;
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
    private Member member;
    private MemberDto.MemberResponseDto memberResponseDto;
    private Attach attach;
    private NoticeBoard noticeBoard;
    private NoticeRequestDto requestDto;
    private NoticeResponseDto responseDto;
    List<AttachDto> detailfileList = new ArrayList<>();
    List<Attach>filelist = new ArrayList<>();

    @BeforeEach
    public void init() throws Exception {
        member = memberDto();
        memberResponseDto = responseDto();
        noticeBoard = noticeBoard();
        requestDto = noticeRequestDto();
        responseDto = NoticeResponseDto();
        attach = attach();
        filelist.add(attach);
        filelist = fileHandler.parseFileInfo(requestDto.getFiles());
        detailfileList.add(attachDto());
        detailfileList = attachService.boardfilelist(noticeBoard.getId());
    }
    @Test
    @DisplayName("공지게시글 목록")
    public void noticeBoardListTest(){
        PageRequest pageRequest = PageRequest.of(0,5, Sort.by("id"));
        given(noticeBoardRepository.findAllList(pageRequest)).willReturn(Page.empty());

        Page<NoticeResponseDto>result = noticeService.noticeAllList(pageRequest);

        Assertions.assertThat(result).isEmpty();
    }
    @Test
    @DisplayName("게시글 단일조회")
    public void noticeBoardDetailTest(){
        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));

        NoticeResponseDto detail = noticeService.findNotice(noticeBoard.getId());

        Assertions.assertThat(detail).isNotNull();
    }
    @Test
    @DisplayName("공지 게시글 검색")
    public void noticeBoardSearchTest(){
        String keyword = "well4149";
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<NoticeResponseDto>list = new ArrayList<>();
        list.add(responseDto);
        Page<NoticeResponseDto>response = new PageImpl<>(list,pageRequest,1);

        given(noticeBoardRepository.findAllSearchList(keyword,pageRequest)).willReturn(response);

        when(noticeService.noticeSearchAll(keyword,pageRequest)).thenReturn(response);
        response = noticeService.noticeSearchAll(keyword,pageRequest);

        assertThat(response.toList().get(0).getNoticeWriter()).isEqualTo(keyword);
    }

    @Test
    @DisplayName("공지 게시글 작성")
    public void noticeBoardWriteTest() throws Exception {
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(noticeBoardRepository.save(noticeBoard)).willReturn(noticeBoard);
        given(fileHandler.parseFileInfo(noticeRequestDto().getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);

        noticeService.noticeCreate(noticeRequestDto(),noticeRequestDto().getFiles());

        Mockito.verify(noticeBoardRepository).save(ArgumentMatchers.any());
        verify(fileHandler,times(2)).parseFileInfo(any());

    }
    @Test
    @DisplayName("게시글 수정")
    public void noticeBoardUpdateTest()throws Exception{

        MockMultipartFile updateFile = new MockMultipartFile("test4", "test4.PNG", MediaType.IMAGE_PNG_VALUE, "test4".getBytes());
        noticeRequestDto().setFiles(List.of(updateFile));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));
        given(fileHandler.parseFileInfo(noticeRequestDto().getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachNoticeBoard(noticeBoard.getId())).willReturn(filelist);

        noticeService.noticeUpdate(noticeBoard.getId(),noticeRequestDto(),noticeRequestDto().getFiles());

        verify(fileHandler,atLeastOnce()).parseFileInfo(any());
    }
    @Test
    @DisplayName("게시글 삭제")
    public void noticeBoardDeleteTest()throws Exception{
        noticeBoard = noticeBoard();
        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(fileHandler.parseFileInfo(noticeRequestDto().getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachNoticeBoard(noticeBoard.getId())).willReturn(filelist);

        attachService.noticefilelist(noticeBoard.getId());
        noticeService.noticeDelete(noticeBoard.getId());

        verify(noticeBoardRepository).deleteById(any());
        verify(fileHandler,times(1)).parseFileInfo(any());
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
    private NoticeRequestDto noticeRequestDto(){
        return NoticeRequestDto
                .builder()
                .noticeGroup("공지게시판")
                .noticeContents("ㅅㄷㄴㅅ")
                .noticeWriter(member.getUserId())
                .fileGroupId("notice_fe2433")
                .isFixed('Y')
                .files(List.of(
                        new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                        new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                        new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())))
                .build();
    }
    private NoticeResponseDto NoticeResponseDto(){
        return NoticeResponseDto
                .builder()
                .id(noticeBoard.getId())
                .noticeTitle(noticeBoard.getNoticeTitle())
                .noticeWriter(member.getUserId())
                .noticeContents(noticeBoard.getNoticeContents())
                .noticeGroup(noticeBoard.getNoticeGroup())
                .fileGroupId(noticeBoard.getFileGroupId())
                .isFixed(noticeBoard.getIsFixed())
                .createdTime(noticeBoard.getCreatedTime())
                .updatedTime(noticeBoard.getUpdatedTime())
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
                .noticeId(1)
                .originFileName("c.jpg")
                .fileSize(30277L)
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .build();
    }
}
