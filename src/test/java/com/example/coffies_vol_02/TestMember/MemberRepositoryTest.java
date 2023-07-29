package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.config.QueryDsl.TestQueryDslConfig;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;

import java.util.Optional;


@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 검색 테스트->회원 아이디")
    public void memberSearchTest(){
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        String keyword= "well4149";

        Page<MemberResponse> result = memberRepository.findByAllSearch(SearchType.i,keyword,pageable);

        System.out.println(result);
    }

    @Test
    @DisplayName("회원 로그인 횟수 테스트->실제로 1이 카운트가 되는가?")
    public void loginfailTest(){

        Optional<Member>member = memberRepository.findById(1);
        Member member1 = member.get();
        int failCount = member1.getFailedAttempt() +1;
        System.out.println(failCount);
        memberRepository.updateFailedAttempts(failCount,member1.getUserId());
        System.out.println(member1.getUserId());
        System.out.println(member1.getFailedAttempt());
        System.out.println("resutl::"+member1);
    }
}
