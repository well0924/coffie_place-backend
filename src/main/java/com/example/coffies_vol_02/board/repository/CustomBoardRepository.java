package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPrevious;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CustomBoardRepository {
    //게시글 목록
    Page<BoardResponse>boardList(Pageable pageable);
    //게시글 검색
    Page<BoardResponse>findAllSearch(String searchVal, Pageable pageable);
    //게시글 조회
    BoardResponse boardDetail(int boardId);
}
