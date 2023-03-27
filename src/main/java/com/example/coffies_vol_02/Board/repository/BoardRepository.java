package com.example.coffies_vol_02.Board.repository;

import com.example.coffies_vol_02.Board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Integer> {

}
