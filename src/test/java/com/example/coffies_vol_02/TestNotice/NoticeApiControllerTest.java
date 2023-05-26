package com.example.coffies_vol_02.TestNotice;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponseDto;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequestDto;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponseDto;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
import com.example.coffies_vol_02.notice.service.NoticeService;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NoticeApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private NoticeService noticeService;

    @Mock
    private NoticeBoardRepository noticeBoardRepository;
    @Mock
    private FileHandler fileHandler;

    @Mock
    private AttachRepository attachRepository;

    private Member member;

    private NoticeBoard noticeBoard;

    private NoticeResponseDto responseDto;

    private NoticeRequestDto requestDto;

    private MemberResponseDto memberResponseDto;

    Attach attach;

    List<AttachDto> detailfileList = new ArrayList<>();

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
        memberResponseDto = responseDto();
        noticeBoard = noticeBoard();
        requestDto = noticeRequestDto();
        responseDto = NoticeResponseDto();
        attach = attach();
        filelist.add(attach);
        filelist = fileHandler.parseFileInfo(noticeRequestDto().getFiles());
        detailfileList.add(attachDto());
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("공지게시판 목록")
    public void NoticeBoardListTest()throws Exception{
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<NoticeResponseDto>list = new ArrayList<>();
        list.add(responseDto);
        Page<NoticeResponseDto>result = new PageImpl<>(list,pageRequest,1);
        given(noticeService.noticeAllList(pageRequest)).willReturn(result);

        when(noticeService.noticeAllList(pageRequest)).thenReturn(result);
        mvc.perform(get("/api/notice/list")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(noticeService,atLeastOnce()).noticeAllList(pageRequest);
    }

    @Test
    @DisplayName("공지게시판 단일 조회")
    public void NoticeBoardDetailTest()throws Exception{

        given(noticeService.findNotice(noticeBoard.getId())).willReturn(responseDto);

        when(noticeService.findNotice(noticeBoard.getId())).thenReturn(responseDto);

        mvc.perform(get("/api/notice/detail/{notice_id}",noticeBoard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(noticeService).findNotice(noticeBoard.getId());
    }

    @Test
    @DisplayName("공지게시판 작성")
    public void NoticeBoardWriteTest()throws Exception{

        when(noticeService.noticeCreate(noticeRequestDto(),noticeRequestDto().getFiles())).thenReturn(noticeBoard.getId());

        mvc.perform(multipart("/api/notice/write")
                        .file("files",noticeRequestDto().getFiles().get(0).getBytes())
                        .file("files",noticeRequestDto().getFiles().get(1).getBytes())
                        .param("noticeGroup",noticeRequestDto().getNoticeGroup())
                        .param("noticeTitle",noticeRequestDto().getNoticeTitle())
                        .param("noticeWriter",noticeRequestDto().getNoticeWriter())
                        .param("noticeContents",noticeRequestDto().getNoticeContents())
                        .param("fileGroupId",noticeRequestDto().getFileGroupId())
                        .param("isFixed",Character.toString(noticeRequestDto().getIsFixed()))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("POST");
                    return requestPostProcessor;}))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("공지게시판 수정")
    public void NoticeBoardUpdateTest()throws Exception{
        MockMultipartFile updateFile = new MockMultipartFile("test4", "test4.PNG", MediaType.IMAGE_PNG_VALUE, "test4".getBytes());
        noticeRequestDto().setFiles(List.of(updateFile));

        given(noticeBoardRepository.findById(noticeBoard.getId())).willReturn(Optional.of(noticeBoard));
        given(fileHandler.parseFileInfo(noticeRequestDto().getFiles())).willReturn(filelist);
        given(attachRepository.save(attach)).willReturn(attach);
        given(attachRepository.findAttachNoticeBoard(noticeBoard.getId())).willReturn(filelist);

        mvc.perform(multipart("/api/notice/update/{notice_id}",noticeBoard.getId())
                .file("files",noticeRequestDto().getFiles().get(0).getBytes())
                .param("noticeGroup","자유게시판")
                .param("noticeTitle","제목")
                .param("noticeWriter","well4149")
                .param("noticeContents","내용 테스트")
                .param("fileGroupId",noticeRequestDto().getFileGroupId())
                .param("isFixed",Character.toString('N'))
                .with(requestPostProcessor->{
                    requestPostProcessor.setMethod("PATCH");
                    return requestPostProcessor;})
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isCreated())
                .andDo(print());

        verify(noticeService).noticeUpdate(anyInt(),any(),any());
    }

    @Test
    @DisplayName("공지게시판 삭제")
    public void NoticeBoardDeleteTest()throws Exception{
        mvc.perform(delete("/api/notice/delete/{notice_id}",noticeBoard.getId())
                        .contentType(MediaType.ALL)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
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
                .role(Role.ROLE_ADMIN)
                .build();
    }
    private MemberResponseDto responseDto(){
        return MemberResponseDto
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
    private NoticeBoard noticeBoard(){
        return NoticeBoard.builder()
                .id(1)
                .noticeTitle("title")
                .noticeWriter(memberDto().getUserId())
                .noticeContents("test")
                .isFixed('Y')
                .noticeGroup("자유게시판")
                .fileGroupId("notice_gr23411")
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
                .noticeBoard(noticeBoard)
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
