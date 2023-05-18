package com.example.coffies_vol_02.Place.repository;

import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomPlaceRepository {
    //가게 검색
    Page<PlaceDto.PlaceResponseDto> placeListSearch(String keyword, Pageable pageable);
    //가게 평점 top5
    Page<PlaceDto.PlaceResponseDto>placeTop5(Pageable pageable);
    //가게 목록 (무한 스크롤)
    Slice<PlaceDto.PlaceResponseDto>placeList(Pageable pageable,String keyword);
}
