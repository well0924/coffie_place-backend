package com.example.coffies_vol_02.favoritePlace.domain.dto;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.querydsl.core.annotations.QueryProjection;

public record FavoritePlaceResponse(
        Integer id,
        Integer placeId,
        Integer memberId,
        String placeName,
        Double reviewRate,
        String placeStart,
        String placeClose,
        String placeAddr1,
        String placeAddr2,
        String isTitle,
        String thumbFileImagePath) {

    @QueryProjection
    public FavoritePlaceResponse(FavoritePlace favoritePlace){
        this(
                favoritePlace.getId(),
                favoritePlace.getPlace().getId(),
                favoritePlace.getMember().getId(),
                favoritePlace.getPlace().getPlaceName(),
                favoritePlace.getPlace().getReviewRate(),
                favoritePlace.getPlace().getPlaceStart(),
                favoritePlace.getPlace().getPlaceClose(),
                favoritePlace.getPlace().getPlaceAddr1(),
                favoritePlace.getPlace().getPlaceAddr2(),
                favoritePlace.getPlace().getPlaceImageList().get(0).getIsTitle(),
                favoritePlace.getPlace().getPlaceImageList().get(0).getThumbFileImagePath());
    }
}
