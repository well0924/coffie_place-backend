package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.BDDMockito.then;
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

    private MemberRequest request;
    private MemberResponse response;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        member = memberDto();
        request = request();
        response = response();
    }

    @DisplayName("회원 목록")
    @Test
    public void memberListTest()throws Exception{
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

        when(memberService.findMemberRecord(member.getId())).thenReturn(response());

        mvc.perform(get("/api/member/detail/{user_idx}",member.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("회원 가입-성공")
    @Test
    public void memberJoinTest()throws Exception{
        String encode = bCryptPasswordEncoder.encode("well31942@!@");
        member.setPassword(bCryptPasswordEncoder.encode(encode));
        given(memberRepository.save(request.toEntity(member))).willReturn(member);

        doNothing().when(memberService).memberCreate(request);

        mvc.perform(post("/api/member/join")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        
        then(memberService).should().memberCreate(request);
    }

    @DisplayName("회원 수정")
    @Test
    public void memberUpdateTest()throws Exception{
        //given
        given(memberRepository.findById(1)).willReturn(Optional.of(member));

        //when
        member.updateMember(request);

        mvc.perform(MockMvcRequestBuilders.patch("/api/member/update/{user_idx}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @DisplayName("회원 삭제")
    @Test
    public void memberDeleteTest()throws Exception{
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));

        doNothing().when(memberService).memberDelete(member.getId());

        mvc.perform(delete("/api/member/delete/{user_idx}", response.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(memberService).memberDelete(any());
    }

    @Test
    @DisplayName("회원아이디 중복-성공")
    public void memberIdDuplicatedTest()throws Exception{

        given(memberService.memberIdCheck(member.getUserId())).willReturn(anyBoolean());

        mvc.perform(
                        get("/api/member/id-check/{user_id}",member.getUserId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).memberIdCheck(member.getUserId());
    }

    @Test
    @DisplayName("회원이메일 중복-중복이 안되는 경우")
    public void memberEmailDuplicatedTest()throws Exception{
        given(memberService.memberEmailCheck(member.getUserEmail())).willReturn(anyBoolean());

        when(memberService.memberEmailCheck(member.getUserEmail())).thenReturn(anyBoolean());

        mvc.perform(get("/api/member/email-check/{user_email}",member.getUserEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).memberEmailCheck(member.getUserEmail());
    }

    @Test
    @DisplayName("회원이메일 중복-중복이 되는 경우")
    public void memberEmailDuplicatedTestFail()throws Exception{
        given(memberService.findMemberRecord(member.getId())).willReturn(response());
        given(memberService.memberEmailCheck(member.getUserEmail())).willReturn(true);

        when(memberService.memberEmailCheck(member.getUserEmail())).thenReturn(true);

        mvc.perform(get("/api/member/email-check/{user_email}",member.getUserEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        verify(memberService).memberEmailCheck(member.getUserEmail());
    }

    @Test
    @DisplayName("회원 아이디 찾기")
    public void memberIdFindTest()throws Exception{
        //given
        String username = member.getMemberName();
        String userEmail = member.getUserEmail();
        given(memberRepository.findByMemberNameAndUserEmail(eq(username),eq(userEmail))).willReturn(Optional.of(member));

        //when
        when(memberService.findUserId(eq(username),eq(userEmail))).thenReturn(member.getUserId());

        mvc.perform(get("/api/member/find-id/{user_name}/{user_email}",username,userEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        verify(memberService).findUserId(eq(username),eq(userEmail));
    }

    @Test
    @DisplayName("회원 비밀번호 재설정")
    public void memberPasswordChangeTest()throws Exception{
        //given
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        int id =member.getId();
        String password = "4567qwer!!";
        request.password(bCryptPasswordEncoder.encode(password));

        //when
        when(memberService.updatePassword(id,request)).thenReturn(member.getId());

        mvc.perform(MockMvcRequestBuilders.patch("/api/member/password/{user_idx}",id)
                        .content(objectMapper.writeValueAsString(request))
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

    private MemberRequest request(){
        return new MemberRequest(
                member.getId(),
                member.getUserId(),
                member.getPassword(),
                member.getMemberName(),
                member.getUserPhone(),
                member.getUserGender(),
                member.getUserAge(),
                member.getUserEmail(),
                member.getUserAddr1(),
                member.getUserAddr2(),
                member.getRole());
    }

    private MemberResponse response(){
        return new MemberResponse(member);
    }
}
