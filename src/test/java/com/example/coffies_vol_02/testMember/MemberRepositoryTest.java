package com.example.coffies_vol_02.testMember;

import com.example.coffies_vol_02.config.queryDsl.TestQueryDslConfig;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("계정잠금 확인 기능 테스트")
    public void existsAllByAccountNonLocked_Test(){
        List<Member>memberList = memberRepository.existsAllByAccountLocked(LocalDateTime.now());

        System.out.println(memberList.toArray().length);

        assertThat(memberList).isEqualTo(memberList);
    }
}
