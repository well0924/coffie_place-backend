package com.example.coffies_vol_02.Board.repository;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBoardRepository {
    //게시글 목록
    Page<BoardDto.BoardResponseDto>boardList(Pageable pageable);
    //게시글 검색
    Page<BoardDto.BoardResponseDto>findAllSearch(String searchVal, Pageable pageable);
}
