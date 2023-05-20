package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
    //회원 검색
    Page<MemberDto.MemberResponseDto>findByAllSearch(String searchVal, Pageable pageable);
}
