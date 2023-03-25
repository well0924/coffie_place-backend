package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Integer> {

    //페이징 목록
    Page<Member> findAll(Pageable pageable);
    //아이디 중복처리
    Boolean existsByUserId(String userId);
    //이메일 중복처리
    Boolean existsByUserEmail(String userEmail);
    //아이디 찾기
    Optional<Member> findByMemberNameAndUserEmail(String membername, String useremail);
    //시큐리티 로그인
    Optional<Member>findByUserId(String userId);
}
