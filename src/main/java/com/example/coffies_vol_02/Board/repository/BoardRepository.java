package com.example.coffies_vol_02.Board.repository;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Integer> {
    //비밀 번호 확인
    @Query("select b from Board b where b.id=:id and b.passWd= :passWd")
    BoardDto.BoardResponseDto findByPassWdAndId(@Param("passWd") String password, @Param("id") Integer id);
}
