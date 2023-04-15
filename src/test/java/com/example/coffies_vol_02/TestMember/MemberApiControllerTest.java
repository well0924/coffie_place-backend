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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
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
        mvc.perform(get("/api/member/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("회원 가입")
    @Test
    public void memberJoinTest()throws Exception{
        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();

        dto.setId(member.getId());
        dto.setUserId(member.getUserId());
        dto.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        dto.setUserGender(member.getUserGender());
        dto.setUserAge(member.getUserAge());
        dto.setMemberName(member.getMemberName());
        dto.setUserEmail(member.getUserEmail());
        dto.setUserPhone(member.getUserPhone());
        dto.setUserAddr1(member.getUserAddr1());
        dto.setUserAddr2(member.getUserAddr2());
        dto.setRole(member.getRole());

        mvc.perform(post("/api/member/memberjoin")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }
    @DisplayName("회원 수정")
    @Test
    public void memberUpdateTest()throws Exception{
        given(memberRepository.findById(1)).willReturn(Optional.of(member));

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();
        dto.setMemberName("update name");
        dto.setUserId("test update");
        dto.setRole(Role.ROLE_USER);
        dto.setUserEmail("testemail123@.com");
        dto.setUserAge("21");
        dto.setUserPhone("02-906-8570");
        dto.setUserGender("여성");

        member.updateMember(dto);

        mvc.perform(MockMvcRequestBuilders.patch("/api/member/memberUpdate/{id}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @DisplayName("회원 삭제")
    @Test
    public void memberDeleteTest()throws Exception{

        mvc.perform(MockMvcRequestBuilders.delete("/api/member/memberDelete/{id}", responseDto.getId())
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
        String password = "4567qwer!!";
        MemberDto.MemberCreateDto dto =  new MemberDto.MemberCreateDto();
        dto.setPassword(bCryptPasswordEncoder.encode(password));
        mvc.perform(MockMvcRequestBuilders.patch("/api/member/newpassword/{id}",id)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("회원선택삭제기능")
    public void memberSelecteDelete() throws Exception {
        List<String> userid = new ArrayList<>();

        userid.add(memberDto().getUserId());

        mvc.perform(post("/api/member/selectdelete")
                        .content(objectMapper.writeValueAsString(userid))
                        .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
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
                .password("qwer4149!@#")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("30")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .build();
    }
}
