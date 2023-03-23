package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberApiControllerTest {

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

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
    public void memberListFailTest()throws Exception{
        mvc.perform(get("/api/member/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("회원 가입")
    @Test
    public void memberJoinTest()throws Exception{

        mvc.perform(post("/api/member/memberjoin")
                        .content(objectMapper.writeValueAsString(memberDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @DisplayName("회원 수정")
    @Test
    public void memberUpdateTest()throws Exception{
        given(memberRepository.findById(2)).willReturn(Optional.of(member));

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();
        dto.setId(member.getId());
        dto.setMemberName("update name");
        dto.setUserId("test update");
        dto.setRole(Role.ROLE_ADMIN);
        dto.setUserEmail("testemail.com");

        mvc.perform(put("/api/member/memberUpdate/{id}",member.getId())
                        .content(objectMapper.writeValueAsString(dto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("회원 삭제")
    @Test
    public void memberDeleteTest()throws Exception{

        mvc.perform(delete("/api/member/memberDelete/{id}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원아이디 중복-성공")
    public void memberIdDuplicatedTest()throws Exception{
        mvc.perform(
                get("/api/member/idduplicated/{id}",member.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원이메일 중복")
    public void memberEmailDuplicatedTest()throws Exception{

        mvc.perform(get("/api/member/emailduplicated/{email}",member.getUserEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 아이디 찾기")
    public void memeberIdFindTest()throws Exception{

        String username = member.getMemberName();
        String userEmail = member.getUserEmail();

        mvc.perform(get("/api/member/findid/{name}/{email}",username,userEmail)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 비밀번호 재설정")
    public void memeberPasswordChangeTest()throws Exception{
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        int id =member.getId();
        String password = "4567";
        MemberDto.MemberCreateDto dto =  new MemberDto.MemberCreateDto();
        dto.setPassword(password);
        mvc.perform(put("/api/member/newpassword/{id}",id)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private MemberDto.MemberResponseDto responseDto(){
        return MemberDto.MemberResponseDto
                .builder()
                .id(1)
                .userId("well4149")
                .password("1234")
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


    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("1234")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .build();
    }
}
