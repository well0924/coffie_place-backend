package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomFavoritePlaceRepository {
    Page<FavoritePlaceResponse> favoritePlaceWishList(Pageable pageable, String userId);
}
