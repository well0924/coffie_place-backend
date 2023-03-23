package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.config.Exception.ERRORCODE;
import com.example.coffies_vol_02.config.Exception.RestApiException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    Member member;

    MemberDto.MemberResponseDto responseDto;

    @BeforeEach
    public void init(){
        member = memberDto();
        responseDto = responseDto();
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

        assertThrows(Exception.class,()->{
            Optional<Member>detail = Optional
                    .ofNullable(
                            memberRepository.findById(0)
                                    .orElseThrow(()->new RestApiException(ERRORCODE.NOT_FOUND_MEMBER)));
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
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(1).orElseThrow(() -> new RestApiException(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = detail.get();

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();
        dto.setId(member.getId());
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
        Optional<Member>detail = Optional.ofNullable(memberRepository.findById(1).orElseThrow(() -> new RestApiException(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = detail.get();
        String changepassword = "4567";

        MemberDto.MemberCreateDto dto = new MemberDto.MemberCreateDto();
        dto.setPassword(changepassword);

        given(memberService.updatePassword(member.getId(),dto)).willReturn(member.getId());

        assertThat(changepassword).isEqualTo(member.getPassword());
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
                .userAge(20)
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
}
