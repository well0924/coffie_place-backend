package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.config.Exception.ERRORCODE;
import com.example.coffies_vol_02.config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    Member member;
    MemberDto.MemberResponseDto responseDto;

    @BeforeEach
    public void init(){
        member = memberDto();
        responseDto = responseDto();
    }
    @Test
    @DisplayName("회원 목록(페이징)")
    public void memberList(){

    }
    @Test
    @DisplayName("회원 단일 조회")
    public void memberDetailTest(){

        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));

        MemberDto.MemberResponseDto dto = memberService.findMemberById(1);

        then(memberService.findMemberById(memberDto().getId()));
        assertThat(dto.getMemberName()).isEqualTo(member.getMemberName());
    }

    @Test
    @DisplayName("회원 단일 조회실패")
    public void memberDetailTestFail(){

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,()->{
            Optional<Member>detail = Optional
                    .ofNullable(
                            memberRepository.findById(0)
                                    .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        });
    }

    @Test
    @DisplayName("회원 가입")
    public void memberJoinTest(){
        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();

        dto.setId(memberDto().getId());
        dto.setPassword(memberDto().getPassword());
        dto.setMemberName(memberDto().getMemberName());
        dto.setUserAge(memberDto().getUserAge());
        dto.setUserGender(memberDto().getUserGender());
        dto.setUserEmail(memberDto().getUserEmail());
        dto.setUserPhone(memberDto().getUserPhone());
        dto.setRole(Role.ROLE_ADMIN);
        dto.setUserAddr1(memberDto().getUserAddr1());
        dto.setUserAddr2(memberDto().getUserAddr2());
        dto.setUserId(memberDto().getUserId());

        given(memberService.memberSave(dto)).willReturn(dto.getId());

        int result = memberService.memberSave(dto);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("회원 수정")
    public void memberUpdateTest(){
        //given
        given(memberRepository.findById(1)).willReturn(Optional.of(member));

        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(1).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = detail.get();

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();

        dto.setMemberName("update name");
        dto.setUserId("test update");
        dto.setRole(Role.ROLE_ADMIN);
        dto.setUserEmail("testemail.com");

        member.updateMember(dto);
        //when
        int result = memberService.memberUpdate(1,dto);
        //then
        then(memberService.memberUpdate(1,dto));
        assertThat(result).isEqualTo(memberDto().getId());
        assertThat(dto.getMemberName()).isEqualTo("update name");
    }

    @Test
    @DisplayName("회원 탈퇴")
    public void memberDeleteTest(){
        //given
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        long count = memberRepository.count();
        //when
        memberService.memberDelete(member.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("회원아이디 중복")
    public void memberIdDuplicatedTest(){
        //given
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        String userid = member.getUserId();

        //when
        given(memberService.existsByUserId(userid)).willReturn(true);
        boolean result=memberService.existsByUserId(userid);

        //then
        then(memberService.existsByUserId(userid));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원이메일 중복")
    public void memberEmailDuplicatedTest(){
        given(memberRepository.findById(memberDto().getId())).willReturn(Optional.of(member));
        String userEmail = member.getUserEmail();

        given(memberService.existByUserEmail(userEmail)).willReturn(true);
        boolean result = memberService.existByUserEmail(userEmail);

        then(memberService.existByUserEmail(userEmail));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원 아이디 찾기")
    public void memberIdFindTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String userName = member.getMemberName();
        String userEmail = member.getUserEmail();

        given(memberRepository.findByMemberNameAndUserEmail(userName,userEmail)).willReturn(Optional.of(member));
        String userId =memberService.findByMembernameAndUseremail(userName,userEmail);

        then(memberService.findByMembernameAndUseremail(userName,userEmail));
        assertThat(userId).isEqualTo(member.getUserId());
    }
    @Test
    @DisplayName("회원 비밀번호 변경")
    public void memberPasswordChangeTest(){

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(1).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = detail.get();

        String changepassword = "4567";

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();
        dto.setPassword(changepassword);
        System.out.println(dto.getPassword());
        given(memberService.updatePassword(member.getId(),dto)).willReturn(member.getId());

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
                .build();

        List<Member>memberlist= new ArrayList<>();
        memberlist.add(member1);
        memberlist.add(member2);

        given(memberRepository.findAll()).willReturn(memberlist);

        for(int i =0;i<memberlist.size();i++){
            memberRepository.deleteAllByUserId(Arrays.asList(member1.getUserId(),member2.getUserId()));
        }

        when(memberRepository.findById(2)).thenReturn(Optional.empty());
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
                .build();
    }

    private MemberDto.MemberResponseDto responseDto(){
        return MemberDto.MemberResponseDto
                .builder()
                .id(1)
                .userId("well4149")
                .password(bCryptPasswordEncoder.encode(member.getPassword()))
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
}
