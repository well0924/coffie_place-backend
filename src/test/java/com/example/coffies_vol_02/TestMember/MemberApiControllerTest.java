package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberApiControllerTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;

}
