package com.example.coffies_vol_02.favoritePlace.domain.dto;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ApiModel(value = "위시리스트 Dto",description = "위시리스트의 Dto")
@Getter
@ToString
@AllArgsConstructor
public class FavoritePlaceResponseDto {
    @Schema(description = "위시리시트의 번호",type = "Integer")
    private Integer id;
    @Schema(description = "가게 번호",type = "Integer")
    private Integer placeId;
    @Schema(description = "회원의 번호",type = "Integer")
    private Integer memberId;
    @Schema(description = "가게의 이름",type = "String")
    private String placeName;
    @Schema(description = "가게의 평점",type = "Double")
    private Double reviewRate;
    @Schema(description = "가게 운영시작시간",type = "String")
    private String placeStart;
    @Schema(description = "가게 운영종료시간",type = "String")
    private String placeClose;
    @Schema(description = "가게 주소1",type = "String")
    private String placeAddr1;
    @Schema(description = "가게 주소2",type = "String")
    private String placeAddr2;
    @Schema(description = "메인 이미지 유무",type = "String")
    private String isTitle;
    @Schema(description = "섬네일 이미지 경로",type = "String")
    private String thumbFileImagePath;

    @Builder
    @QueryProjection
    public FavoritePlaceResponseDto (FavoritePlace favoritePlace){
        this.id = favoritePlace.getId();
        this.memberId = favoritePlace.getMember().getId();
        this.placeId = favoritePlace.getPlace().getId();
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
