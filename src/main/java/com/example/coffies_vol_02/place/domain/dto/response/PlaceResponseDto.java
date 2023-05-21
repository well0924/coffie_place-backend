package com.example.coffies_vol_02.place.domain.dto.response;

import com.example.coffies_vol_02.place.domain.Place;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponseDto {
    private Integer id;
    private Double placeLng;
    private Double placeLat;
    private Double reviewRate;
    private String placeName;
    private String placeAddr1;
    private String placeAddr2;
    private String placePhone;
    private String placeAuthor;
    private String placeStart;
    private String placeClose;
    private String fileGroupId;
    private String isTitle;
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
        //만약 이미지가 없이 가게를 등록을 한 경우에는 기본 이미지로 나오게끔 해야 됨...
        this.thumbFileImagePath = place.getPlaceImageList().get(0).getThumbFileImagePath();
    }
}
