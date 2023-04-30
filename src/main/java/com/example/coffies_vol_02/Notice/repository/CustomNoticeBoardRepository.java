package com.example.coffies_vol_02.Notice.repository;

import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNoticeBoardRepository {
    //공지게시판 목록
    Page<NoticeBoardDto.BoardResponseDto>findAllList(Pageable pageable);
    //공지게시판 검색
    Page<NoticeBoardDto.BoardResponseDto> findAllSearchList(String searchVal, Pageable pageable);
}
