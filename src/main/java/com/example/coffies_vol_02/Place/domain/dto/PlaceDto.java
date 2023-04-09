package com.example.coffies_vol_02.Place.domain.dto;

import com.example.coffies_vol_02.Place.domain.Place;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class PlaceDto {

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceRequestDto{
        private Double placeLng;
        private Double placeLat;
        @NotBlank(message = "가게이름을 입력해주세요.")
        private String placeName;
        @NotBlank(message = "주소를 입력해주세요.")
        private String placeAddr1;
        private String placeAddr2;
        @NotBlank(message = "가게 전화번호를 입력해주세요.")
        private String placePhone;
        private String placeAuthor;
        @NotBlank(message = "가게 시작시간을 입력해주세요.")
        private String placeStart;
        @NotBlank(message = "가게 종료시간을 입력해주세요.")
        private String placeClose;
        private String fileGroupId;
    }
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceResponseDto{
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

        @Builder
        public PlaceResponseDto(Place place){
            this.id = place.getId();
            this.placeAuthor = place.getPlaceAuthor();
            this.placeLat = place.getPlaceLat();
            this.placeLng = place.getPlaceLng();
            this.placeAddr1 = place.getPlaceAddr1();
            this.placeAddr2 = place.getPlaceAddr2();
            this.placeName = place.getPlaceName();
            this.placeStart = place.getPlaceStart();
            this.placeClose = place.getPlaceClose();
            this.placePhone = place.getPlacePhone();
        }
    }
}
