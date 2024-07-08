package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomPlaceRepository {
    //가게 검색
    Slice<PlaceResponseDto> placeListSearch(SearchType searchType, String keyword, Pageable pageable);
    //가게 평점 top5
    List<PlaceResponseDto> placeTop5();
    //가게 목록 (무한 스크롤)
    Slice<PlaceResponseDto>placeList(Pageable pageable,String keyword);
}
