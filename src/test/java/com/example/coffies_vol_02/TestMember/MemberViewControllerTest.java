package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("MemberController Test")
public class MemberViewControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인 화면")
    public void loginPageTest() throws Exception {
        mvc.perform(get("/page/login/loginPage")
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/login/loginpage"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입화면")
    public void memberJoinPageTest()throws Exception{
        mvc.perform(get("/page/login/memberjoin")
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/login/memberjoin"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 아이디 찾기 및 회원 비밀번호 재수정 페이지")
    public void memberIdFindPageTest()throws Exception{
        mvc.perform(get("/page/login/tmpid")
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/login/searchId"))
                .andDo(print());
    }

    @Test
    @DisplayName("어드민 목록 페이지-성공")
    public void adminMemberListPageTest()throws Exception{
        given(memberService.findAll(any(Pageable.class))).willReturn(Page.empty());
        given(memberService.findByAllSearch(any(),any(Pageable.class))).willReturn(Page.empty());

        when(memberService.findByAllSearch(any(),any(Pageable.class))).thenReturn(Page.empty());

        mvc.perform(
                get("/page/admin/adminlist")
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("memberlist"))
                .andExpect(view().name("/admin/adminlist"))
                .andDo(print());

        then(memberService).should().findByAllSearch(any(),any(Pageable.class));
    }

    @Test
    @DisplayName("회원 수정 화면")
    @WithMockUser(username = "well4149",roles = "ADMIN")
    public void adminDetailPageTest()throws Exception{
        given(memberService.findMemberRecord(anyInt())).willReturn(response());

        mvc.perform(
                get("/page/login/modify/{id}",responseDto().id())
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("detail"))
                .andExpect(view().name("/login/membermodify"))
                .andDo(print());
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

    private MemberResponse responseDto(){
        return new MemberResponse(memberDto());
    }
    private MemberResponse response(){
        return new MemberResponse(memberDto());
    }
}
