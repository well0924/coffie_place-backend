package com.example.coffies_vol_02.Member.repository;

import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
    //회원 검색
    Page<MemberDto.MemberResponseDto>findByAllSearch(String searchVal, Pageable pageable);
}
