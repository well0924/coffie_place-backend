package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBoardRepository {
    //게시글 목록
    Page<BoardResponseDto>boardList(Pageable pageable);
    //게시글 검색
    Page<BoardResponseDto>findAllSearch(String searchVal, Pageable pageable);
}
