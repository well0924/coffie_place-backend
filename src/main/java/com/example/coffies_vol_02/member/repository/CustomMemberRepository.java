package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {

    /**
     * 회원 검색
     * 어드민 페이지에서 회원을 검색할 때 사용
     * @author 양경빈
     * @param searchVal 검색어
     * @param pageable 목록 페이징에 필요한 객체
     **/
    Page<MemberResponse>findByAllSearch(SearchType searchType, String searchVal, Pageable pageable);
}
