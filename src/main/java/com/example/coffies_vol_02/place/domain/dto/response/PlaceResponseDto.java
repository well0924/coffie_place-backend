package com.example.coffies_vol_02.place.domain.dto.response;

import com.example.coffies_vol_02.place.domain.Place;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@ApiModel(description = "가게 응답 dto",value = "가게 응답 dto")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponseDto {
    @Schema(description = "가게 번호",type = "Integer")
    private Integer id;
    @Schema(description = "가게 위도",type = "Double")
    private Double placeLng;
    @Schema(description = "가게 경도",type = "Double")
    private Double placeLat;
    @Schema(description = "가게 평점",type = "Double")
    private Double reviewRate;
    @Schema(description = "가게 이름",type = "String")
    private String placeName;
    @Schema(description = "가게 주소1",type = "String")
    private String placeAddr1;
    @Schema(description = "가게 주소2",type = "String")
    private String placeAddr2;
    @Schema(description = "가게 전화번호",type = "String")
    private String placePhone;
    @Schema(description = "가게 작성자",type="String")
    private String placeAuthor;
    @Schema(description = "가게 영업시작 시간",type = "String")
    private String placeStart;
    @Schema(description = "가게 영업종료시간",type = "String")
    private String placeClose;
    @Schema(description = "파일 그룹아이디",type = "String")
    private String fileGroupId;
    @Schema(description = "이미지 고정 여부",type = "String")
    private String isTitle;
    @Schema(description = "원본 이미지 경로",type = "String")
    private String imgPath;
    @Schema(description = "섬네일 이미지 경로",type = "String")
    private String thumbFileImagePath;

    @Builder
    public PlaceResponseDto(Place place){
        this.id = place.getId();
        this.placeAuthor = place.getPlaceAuthor();
        this.placeLat = place.getPlaceLat();
        this.placeLng = place.getPlaceLng();
        this.placeAddr1 = place.getPlaceAddr1();
        this.reviewRate= place.getReviewRate();
        this.placeAddr2 = place.getPlaceAddr2();
        this.placeName = place.getPlaceName();
        this.placeStart = place.getPlaceStart();
        this.placeClose = place.getPlaceClose();
        this.placePhone = place.getPlacePhone();
        this.fileGroupId = place.getFileGroupId();
        this.isTitle = place.getPlaceImageList().get(0).getIsTitle();
        this.thumbFileImagePath = place.getPlaceImageList().get(0).getThumbFileImagePath();
    }
}
