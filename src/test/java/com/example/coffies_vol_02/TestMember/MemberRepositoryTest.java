package com.example.coffies_vol_02.TestMember;

import com.example.coffies_vol_02.Config.TestQueryDslConfig;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.QMember;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.repository.CustomMemberRepository;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 검색 테스트")
    public void memberSearchTest(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        QMember qMember = QMember.member;

        String keyword= "well4149";

        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression booleanExpression = qMember.userId.contains(keyword);
        builder.and(booleanExpression);

        Page<MemberDto.MemberResponseDto> result = memberRepository.findByAllSearch(keyword,pageable);
        Page<Member>result2 = memberRepository.findAll(builder,pageable);
        System.out.println(result.get().collect(Collectors.toList()));
        assertThat(result);
        assertThat(result2.get().toList().get(0).getUserId()).isEqualTo(keyword);
    }
}
