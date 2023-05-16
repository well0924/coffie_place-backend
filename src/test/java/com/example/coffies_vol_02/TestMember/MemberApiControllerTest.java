package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import com.example.coffies_vol_02.Member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberApiControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mvc;

    private Member member;

    private MemberDto.MemberResponseDto responseDto;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        member = memberDto();
        responseDto = responseDto();
    }

    @DisplayName("회원 목록")
    @Test
    public void memberListTest()throws Exception{
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        given(memberRepository.findAll(any(Pageable.class))).willReturn(Page.empty());

        when(memberService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mvc.perform(get("/api/member/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService,atLeastOnce()).findAll(any());
    }

    @DisplayName("회원 조회")
    @Test
    public void memberDetailTest()throws Exception{

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        when(memberService.findMemberById(member.getId())).thenReturn(responseDto);

        mvc.perform(get("/api/member/detail/{user_idx}",member.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).findMemberById(member.getId());
    }

    @DisplayName("회원 가입-성공")
    @Test
    public void memberJoinTest()throws Exception{

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();

        dto.setId(member.getId());
        dto.setUserId(member.getUserId());
        dto.setPassword(member.getPassword());
        dto.setUserGender(member.getUserGender());
        dto.setUserAge(member.getUserAge());
        dto.setMemberName(member.getMemberName());
        dto.setUserEmail(member.getUserEmail());
        dto.setUserPhone(member.getUserPhone());
        dto.setUserAddr1(member.getUserAddr1());
        dto.setUserAddr2(member.getUserAddr2());
        dto.setRole(member.getRole());

        given(memberService.memberSave(eq(dto))).willReturn(member.getId());

        when(memberService.memberSave(eq(dto))).thenReturn(member.getId());

        mvc.perform(post("/api/member/join")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        
        verify(memberService).memberSave(any());
    }

    @DisplayName("회원 수정")
    @Test
    public void memberUpdateTest()throws Exception{
        //given
        given(memberRepository.findById(1)).willReturn(Optional.of(member));

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();
        dto.setMemberName("update name");
        dto.setUserId("test update");
        dto.setRole(Role.ROLE_USER);
        dto.setUserEmail("testemail123@.com");
        dto.setUserAge("21");
        dto.setUserPhone("02-906-8570");
        dto.setUserGender("여성");

        //when
        member.updateMember(dto);

        mvc.perform(MockMvcRequestBuilders.patch("/api/member/update/{user_idx}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        //then
        verify(memberService,atLeast(1)).memberUpdate(anyInt(),any());
    }

    @DisplayName("회원 삭제")
    @Test
    public void memberDeleteTest()throws Exception{
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));

        doNothing().when(memberService).memberDelete(member.getId());

        mvc.perform(delete("/api/member/delete/{user_idx}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(memberService).memberDelete(any());
    }

    @Test
    @DisplayName("회원아이디 중복-성공")
    public void memberIdDuplicatedTest()throws Exception{

        given(memberService.existsByUserId(member.getUserId())).willReturn(anyBoolean());

        mvc.perform(
                        get("/api/member/id-check/{user_id}",member.getUserId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).existsByUserId(member.getUserId());
    }

    @Test
    @DisplayName("회원이메일 중복-중복이 안되는 경우")
    public void memberEmailDuplicatedTest()throws Exception{
        given(memberService.existByUserEmail(member.getUserEmail())).willReturn(anyBoolean());

        when(memberService.existByUserEmail(member.getUserEmail())).thenReturn(anyBoolean());

        mvc.perform(get("/api/member/email-check/{user_email}",member.getUserEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).existByUserEmail(member.getUserEmail());
    }

    @Test
    @DisplayName("회원이메일 중복-중복이 되는 경우")
    public void memberEmailDuplicatedTestFail()throws Exception{
        given(memberService.findMemberById(member.getId())).willReturn(responseDto);
        given(memberService.existByUserEmail(member.getUserEmail())).willReturn(true);

        when(memberService.existByUserEmail(member.getUserEmail())).thenReturn(true);

        mvc.perform(get("/api/member/email-check/{user_email}",member.getUserEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).existByUserEmail(member.getUserEmail());
    }

    @Test
    @DisplayName("회원 아이디 찾기")
    public void memberIdFindTest()throws Exception{
        //given
        String username = member.getMemberName();
        String userEmail = member.getUserEmail();
        given(memberRepository.findByMemberNameAndUserEmail(eq(username),eq(userEmail))).willReturn(Optional.of(member));

        //when
        when(memberService.findByMemberNameAndUserEmail(eq(username),eq(userEmail))).thenReturn(member.getUserId());

        mvc.perform(get("/api/member/find-id/{user_name}/{user_email}",username,userEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        verify(memberService).findByMemberNameAndUserEmail(eq(username),eq(userEmail));
    }

    @Test
    @DisplayName("회원 비밀번호 재설정")
    public void memberPasswordChangeTest()throws Exception{
        //given
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        int id =member.getId();
        String password = "4567qwer!!";
        MemberDto.MemberCreateDto dto =  new MemberDto.MemberCreateDto();
        dto.setPassword(bCryptPasswordEncoder.encode(password));

        //when
        when(memberService.updatePassword(id,dto)).thenReturn(member.getId());

        mvc.perform(MockMvcRequestBuilders.patch("/api/member/password/{user_idx}",id)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        //then
        verify(memberService,atLeast(1)).updatePassword(anyInt(),any());
    }

    @Test
    @DisplayName("회원선택삭제기능")
    public void memberSelecteDelete() throws Exception {
        //given
        List<String> userid = new ArrayList<>();
        userid.add(member.getUserId());
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        doNothing().when(memberService).selectMemberDelete(userid);

        mvc.perform(post("/api/member/select-delete")
                        .content(objectMapper.writeValueAsString(userid))
                        .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(memberService,times(1)).selectMemberDelete(userid);
    }

    private MemberDto.MemberResponseDto responseDto(){
        return MemberDto.MemberResponseDto
                .builder()
                .id(1)
                .userId("well4149")
                .password("1234")
                .memberName("admin1")
                .userEmail("well41492@gmail.com")
                .userPhone("010-9999-9999")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!@#")
                .memberName("admin1")
                .userEmail("well41492@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("30")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .build();
    }
}
