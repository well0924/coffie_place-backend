package com.example.coffies_vol_02.notice.repository;

import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNoticeBoardRepository {
    //공지게시판 목록
    Page<NoticeResponseDto>findAllList(Pageable pageable);
    //공지게시판 검색
    Page<NoticeResponseDto> findAllSearchList(String searchVal, Pageable pageable);
}
