package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.request.MemberRequest;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    Member member;

    MemberRequest memberRequest;
    MemberResponse memberResponse;

    @BeforeEach
    public void init(){
        member = memberDto();
        memberRequest = request();
        memberResponse = response();
    }

    @Test
    @DisplayName("회원 목록(페이징)")
    public void memberList(){

        List<Member>list = new ArrayList<>();
        list.add(member);
        PageRequest pageable = PageRequest.of(0,5, Sort.by("id").descending());
        Page<Member>pageList = new PageImpl<>(list,pageable,0);

        when(memberRepository.findAll(pageable)).thenReturn(pageList);

        Page<MemberResponse>result = memberService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.get().toList().get(0).memberName()).isEqualTo(memberDto().getMemberName());
    }

    @Test
    @DisplayName("회원 단일 조회")
    public void memberDetailTest(){

        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));

        memberResponse = memberService.findByMember(member.getId());

        assertThat(memberResponse.memberName()).isEqualTo(member.getMemberName());
    }

    @Test
    @DisplayName("회원 단일 조회실패")
    public void memberDetailTestFail(){

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,()->{
            Optional<Member>detail = Optional.ofNullable(memberRepository.findById(0).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        });
    }

    @Test
    @DisplayName("회원 검색")
    public void memberSearchTest(){
        String keyword = "well4149";

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<MemberResponse>list = new ArrayList<>();
        list.add(response());

        Page<MemberResponse>result = new PageImpl<>(list,pageRequest,1);

        given(memberRepository.findByAllSearch(keyword,pageRequest)).willReturn(result);

        when(memberService.findByAllSearch(keyword,pageRequest)).thenReturn(result);
        result = memberService.findByAllSearch(keyword,pageRequest);

        assertThat(result.toList().get(0).userId()).isEqualTo(keyword);
    }

    @Test
    @DisplayName("회원가입")
    public void memberCreateRecordTest(){
        MemberRequest result = new MemberRequest(member.getId(),member.getUserId(),null,member.getMemberName(),member.getUserPhone(),member.getUserGender(),member.getUserAge(),member.getUserEmail(),member.getUserAddr1(),member.getUserAddr2(),member.getMemberLat(),member.getMemberLng(),member.getRole());
        member.setPassword(bCryptPasswordEncoder.encode("well31942@!@"));

        given(memberRepository.save(result.toEntity(member))).willReturn(member);

        memberService.memberCreate(result);
    }

    @Test
    @DisplayName("회원 수정")
    public void memberUpdateTest(){
        //given
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        //when
        member.updateMember(request());

        memberService.memberUpdate(member.getId(),request());

    }

    @Test
    @DisplayName("회원 탈퇴")
    public void memberDeleteTest(){
        //given
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        long count = memberRepository.count();
        //when
        memberService.memberDelete(member.getId());
        //then
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("회원아이디 중복")
    public void memberIdDuplicatedTest(){
        //given
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        String userid = member.getUserId();

        //when
        given(memberService.memberIdCheck(userid)).willReturn(true);
        boolean result=memberService.memberIdCheck(userid);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원이메일 중복")
    public void memberEmailDuplicatedTest(){
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        String userEmail = member.getUserEmail();

        given(memberService.memberEmailCheck(userEmail)).willReturn(true);
        boolean result = memberService.memberEmailCheck(userEmail);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원 아이디 찾기")
    public void memberIdFindTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String userName = member.getMemberName();
        String userEmail = member.getUserEmail();

        given(memberRepository.findByMemberNameAndUserEmail(userName,userEmail)).willReturn(Optional.of(member));
        String userId =memberService.findUserId(userName,userEmail);

        assertThat(userId).isEqualTo(member.getUserId());
    }

    @Test
    @DisplayName("회원 비밀번호 변경")
    public void memberPasswordChangeTest(){

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(1).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = detail.orElse(null);

        String changepassword = "4567";

        memberRequest.password(changepassword);

        given(memberService.updatePassword(member.getId(),memberRequest)).willReturn(member.getId());

        assertThat(changepassword).isEqualTo("4567");
    }

    @Test
    @DisplayName("회원 선택삭제")
    public void selectDeleteMemberTest() {

        Member member1 = Member
                .builder()
                .id(2)
                .password(bCryptPasswordEncoder.encode("qwervwvsv!!@w"))
                .memberName("test1")
                .userAge("12")
                .userPhone("010-8272-1234")
                .userEmail("welvsdvnj@gmail.com")
                .userId("well123")
                .userGender("남성")
                .userAddr1("ㅈㄷㄱㅈㄷㄱㅈㄷㄱㅈㄷㄱㄷㅈㄱ")
                .userAddr2("ㄴㅇㄹㄴㅇㄹㅇㄴㄹ")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .build();

        Member member2 = Member
                .builder()
                .id(3)
                .userId("wevdvd")
                .password(bCryptPasswordEncoder.encode("sdfsdf!!"))
                .memberName("tester2")
                .userAge("32")
                .userEmail("wellvsdvn@naver.com")
                .userGender("여성")
                .userAddr1("ㄴㅇ루ㅏㅓ푸너ㅏㅍㄴ")
                .userAddr2("ㄴㅇㄹㄴㅇㄹ")
                .userPhone("010-2323-3432")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .build();

        List<Member>memberlist= new ArrayList<>();
        memberlist.add(member1);
        memberlist.add(member2);

        List<String>deleteList = new ArrayList<>();
        deleteList.add(member1.getUserId());
        deleteList.add(member2.getUserId());

        given(memberRepository.save(member1)).willReturn(member1);
        given(memberRepository.save(member2)).willReturn(member2);
        given(memberRepository.findAll()).willReturn(memberlist);

        //when
        doNothing().when(memberRepository).deleteAllByUserId(deleteList);

        memberService.selectMemberDelete(deleteList);

        //then
        verify(memberRepository,times(2)).deleteAllByUserId(deleteList);
    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password(bCryptPasswordEncoder.encode("qwer4149!!"))
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .accountNonLocked(true)
                .failedAttempt(0)
                .lockTime(new Date())
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
                member.getMemberLat(),
                member.getMemberLng(),
                member.getRole());
    }

    private MemberResponse response(){
        return new MemberResponse(member);
    }
}
