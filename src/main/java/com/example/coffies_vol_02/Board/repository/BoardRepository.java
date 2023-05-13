package com.example.coffies_vol_02.Board.repository;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Integer>,CustomBoardRepository,QuerydslPredicateExecutor {
    //비밀 번호 확인
    @Query("select b from Board b where b.id=:id and b.passWd= :passWd")
    BoardDto.BoardResponseDto findByPassWdAndId(@Param("passWd") String password, @Param("id") Integer id);

    //내가 작성한 글
    Page<Board> findByMember(Member member, Pageable pageable);

    //게시글 조회수 확인
    @Query("select b.readCount from Board b where b.id = :id")
    Integer ReadCount(@Param("id") Integer id);
    
    //게시글 조회수 증가
    @Transactional
    @Modifying
    @Query("update Board b set b.readCount = :readCount where b.id = :id")
    void ReadCountUpToDB(@Param("id")Integer id, @Param("readCount")Integer readCount);
}
