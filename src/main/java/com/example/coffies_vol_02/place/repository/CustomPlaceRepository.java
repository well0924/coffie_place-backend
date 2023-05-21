package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomPlaceRepository {
    //가게 검색
    Page<PlaceResponseDto> placeListSearch(String keyword, Pageable pageable);
    //가게 평점 top5
    Page<PlaceResponseDto>placeTop5(Pageable pageable);
    //가게 목록 (무한 스크롤)
    Slice<PlaceResponseDto>placeList(Pageable pageable,String keyword);
}
