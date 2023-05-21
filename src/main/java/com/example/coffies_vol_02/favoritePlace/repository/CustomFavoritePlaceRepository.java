package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomFavoritePlaceRepository {
    Page<FavoritePlaceDto> favoritePlaceWishList(Pageable pageable,String userId);
}
