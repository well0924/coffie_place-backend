package com.example.coffies_vol_02.favoritePlace.domain.dto;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
@Getter
@NoArgsConstructor
public class FavoritePlaceDto {
    private Integer id;
    private Integer placeId;
    private Integer memberId;
    //위시리스트에서 보여줄 부분
    private String placeName;
    private Double reviewRate;
    private String placeStart;
    private String placeClose;
    private String placeAddr1;
    private String placeAddr2;
    private String isTitle;
    private String thumbFileImagePath;
    @Builder
    @QueryProjection
    public FavoritePlaceDto(FavoritePlace favoritePlace){
        this.id = favoritePlace.getId();
        this.placeId = favoritePlace.getPlace().getId();
        this.memberId = favoritePlace.getMember().getId();
        this.placeName = favoritePlace.getPlace().getPlaceName();
        this.reviewRate = favoritePlace.getPlace().getReviewRate();
        this.placeStart = favoritePlace.getPlace().getPlaceStart();
        this.placeClose = favoritePlace.getPlace().getPlaceClose();
        this.placeAddr1 = favoritePlace.getPlace().getPlaceAddr1();
        this.placeAddr2 = favoritePlace.getPlace().getPlaceAddr2();
        this.isTitle = favoritePlace.getPlace().getPlaceImageList().get(0).getIsTitle();
        this.thumbFileImagePath = favoritePlace.getPlace().getPlaceImageList().get(0).getThumbFileImagePath();
    }
}

