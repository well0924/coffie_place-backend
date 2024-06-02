package com.example.coffies_vol_02.factory;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;

public class FavoritePlaceFactory {
    public static FavoritePlace favoritePlace(){
        return FavoritePlace
                .builder()
                .id(1)
                .place(PlaceFactory.place())
                .member(MemberFactory.memberDto())
                .fileGroupId(PlaceFactory.place().getFileGroupId())
                .build();
    }
    public static FavoritePlaceResponseDto favoritePlaceResponseDto(){
        return FavoritePlaceResponseDto
                .builder()
                .favoritePlace(favoritePlace())
                .build();
    }
}
