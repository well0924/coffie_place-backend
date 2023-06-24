package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomFavoritePlaceRepository {
    /**
     * 마이페이지 위시리스트 목록
     **/
    Page<FavoritePlaceResponseDto> favoritePlaceWishList(Pageable pageable, String userId);
}
