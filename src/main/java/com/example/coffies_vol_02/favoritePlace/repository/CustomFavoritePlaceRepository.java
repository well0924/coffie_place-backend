package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomFavoritePlaceRepository {
    Page<FavoritePlaceResponseDto> favoritePlaceWishList(Pageable pageable, String userId);
}
