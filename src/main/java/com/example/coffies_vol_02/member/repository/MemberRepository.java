package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Integer>,CustomMemberRepository, QuerydslPredicateExecutor {

    /**
     * 페이징 목록
     **/
    Page<Member> findAll(Pageable pageable);

    /**
     * 아이디 중복처리
     **/
    Boolean existsByUserId(String userId);

    /**
     * 이메일 중복처리
     **/
    Boolean existsByUserEmail(String userEmail);

    /**
     * 아이디 찾기
     **/
    Optional<Member> findByMemberNameAndUserEmail(String memberName, String userEmail);

    /**
     * 시큐리티 로그인
     **/
    Optional<Member>findByUserId(String userId);

    /**
     * 회원 선택삭제
     **/
    @Transactional
    @Modifying
    @Query("delete from Member m where m.userId in :ids")
    void deleteAllByUserId(List<String>ids);
}
