package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.config.QueryDsl.TestQueryDslConfig;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
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
    @DisplayName("회원 비밀번호 로그인 실패시 카운트 테스트")
    public void passwordFailCountTest(){
        Optional<Member>deatil = memberRepository.findById(1);
        int countResult = memberRepository.failAttemptsCount(deatil.get().getUserId());
        System.out.println("::"+countResult);

        int count =memberRepository.updateFailedAttempts(deatil.get().getUserId());
        System.out.println(count);
        int afterFailCount = memberRepository.failAttemptsCount(deatil.get().getUserId());

        System.out.println("result::"+afterFailCount);

        deatil.get().setFailedAttempt(count);
        memberRepository.save(deatil.get());
        System.out.println("최종 결과::"+deatil.get().getFailedAttempt());
    }
}
