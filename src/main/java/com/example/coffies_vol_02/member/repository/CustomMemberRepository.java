package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
    //회원 검색
    Page<MemberResponse>findByAllSearch(String searchVal, Pageable pageable);
}
