package com.example.coffies_vol_02.Place.domain.dto;

import com.example.coffies_vol_02.Place.domain.Place;
import lombok.*;

public class PlaceDto {

    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceRequestDto{
        private Double placeLng;
        private Double placeLat;
        private String placeName;
        private String placeAddr1;
        private String placeAddr2;
        private String placePhone;
        private String placeAuthor;
        private String placeStart;
        private String placeClose;
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
