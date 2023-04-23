package com.example.coffies_vol_02.Board.repository;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBoardRepository {
    Page<BoardDto.BoardResponseDto>findAllSearch(String searchVal, Pageable pageable);
}
