package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.dto.response.MemberResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
    //회원 검색
    Page<MemberResponseDto>findByAllSearch(String searchVal, Pageable pageable);
}
