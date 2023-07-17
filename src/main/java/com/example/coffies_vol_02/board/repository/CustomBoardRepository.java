package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.config.constant.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBoardRepository {

    /**
     * 게시글 목록
     **/
    Page<BoardResponse>boardList(Pageable pageable);

    /**
     * 게시글 검색
     **/
    Page<BoardResponse>findAllSearch(SearchType searchType, String searchVal, Pageable pageable);

    /**
     * 게시글 조회
     **/
    BoardResponse boardDetail(int boardId);
}
