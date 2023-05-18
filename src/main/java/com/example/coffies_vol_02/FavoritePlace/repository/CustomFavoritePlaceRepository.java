package com.example.coffies_vol_02.FavoritePlace.repository;

import com.example.coffies_vol_02.FavoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomFavoritePlaceRepository {
    Page<FavoritePlaceDto.FavoriteResponseDto> favoritePlaceWishList(Pageable pageable,String userId);
}
