package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
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
     * 어드민 페이지에서 회원을 선택삭제하는 기능
     **/
    @Transactional
    @Modifying
    @Query("delete from Member m where m.userId in :ids")
    void deleteAllByUserId(List<String>ids);

    /**
     * 로그인 실패 카운트 확인
     **/
    @Query("select m.failedAttempt from Member m where m.userId = :userIdx")
    Integer failAttemptsCount(String userIdx);

    /**
     * 로그인 실패 카운트
     * 회원 비밀번호 3회 실패시 계정을 30분 동안 잠금.
     **/
    @Transactional
    @Modifying(flushAutomatically = true,clearAutomatically = true)
    @Query("UPDATE Member m SET m.failedAttempt = m.failedAttempt + 1  WHERE m.userId = :userId")
    Integer updateFailedAttempts(@Param("userId") String userId);
}
