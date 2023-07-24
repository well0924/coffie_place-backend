package com.example.coffies_vol_02.TestNotice;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.config.constant.Role;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
import com.example.coffies_vol_02.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NoticeControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private NoticeBoardRepository noticeBoardRepository;
    @Mock
    private NoticeService noticeBoardService;
    @Mock
    private AttachRepository attachRepository;
    @Mock
    private AttachService attachService;
    @Autowired
    private MockMvc mvc;
    Member member;
    NoticeBoard noticeBoard;
    Attach attach;
    NoticeResponse responseDto;
    NoticeRequest requestDto;
    List<AttachDto> detailfileList = new ArrayList<>();
    List<Attach>filelist = new ArrayList<>();
    List<MultipartFile>files = List.of(
            new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
            new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
            new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes()));

    private CustomUserDetails customUserDetails;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        noticeBoard = noticeBoard();
        requestDto = request();
        responseDto =  response();
        attach = attach();
        attach = attach();
        filelist.add(attach);
        detailfileList.add(attachDto());
        detailfileList = attachService.noticefilelist(noticeBoard.getId());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("공지게시판 목록 화면")
    public void noticeBoardListTest()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(noticeBoardService.noticeAllList(any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/page/notice/list")
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("noticelist"))
                .andExpect(view().name("/notice/noticelist"))
                .andDo(print());
    }

    @Test
    @DisplayName("공지게시판 조회 화면")
    public void noticeBoardDetailTest()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(noticeBoardRepository.findById(noticeBoard().getId())).willReturn(Optional.of(noticeBoard));
        given(attachRepository.findAttachNoticeBoard(noticeBoard().getId())).willReturn(filelist);
        given(noticeBoardService.findNotice(noticeBoard.getId())).willReturn(response());

        mvc.perform(get("/page/notice/detail/{notice_id}",noticeBoard.getId())
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("detail"))
                .andExpect(model().attributeExists("filelist"))
                .andExpect(view().name("/notice/noticedetail"))
                .andDo(print());
    }

    @Test
    @DisplayName("공지게시판 작성 화면")
    public void noticeBoardWriteTest()throws Exception{
        mvc.perform(get("/page/notice/writePage"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("fileGroupId"))
                .andExpect(view().name("/notice/noticewrite"))
                .andDo(print());
    }

    @Test
    @DisplayName("공지게시판 수정 및 삭제 화면")
    public void noticeBoardUpdateAndDeleteTest() throws Exception {
        mvc.perform(get("/page/notice/modifyPage/{notice_id}",noticeBoard().getId())
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("detail"))
                .andExpect(view().name("/notice/noticemodify"))
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
    private NoticeBoard noticeBoard(){
        return NoticeBoard
                .builder()
                .id(1)
                .noticeGroup("공지게시판")
                .noticeTitle("제목")
                .noticeContents("ㄹㄷㄹㄷㄹ")
                .noticeWriter(member.getUserId())
                .fileGroupId("notice_ge2353w")
                .isFixed('Y')
                .build();
    }
    private NoticeRequest request(){
        return new NoticeRequest(noticeBoard.getNoticeGroup(),
                noticeBoard.getIsFixed(),
                noticeBoard.getNoticeTitle(),
                noticeBoard.getNoticeWriter(),
                noticeBoard.getNoticeContents(),
                noticeBoard.getFileGroupId());
    }
    private NoticeResponse response(){
        return new NoticeResponse(noticeBoard);
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
