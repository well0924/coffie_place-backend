package com.example.coffies_vol_02.Notice.repository;

import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNoticeBoardRepository {
    Page<NoticeBoardDto.BoardResponseDto> findAllSearchList(String searchVal, Pageable pageable);
}
