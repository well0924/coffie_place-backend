package com.example.coffies_vol_02.place.domain.dto.response;

import com.example.coffies_vol_02.place.domain.Place;

public record PlaceResponse(
        Integer id,
        Double placeLng,
        Double placeLat,
        Double reviewRate,
        String placeName,
        String placeAddr1,
        String placeAddr2,
        String placePhone,
        String placeAuthor,
        String placeStart,
        String placeClose,
        String fileGroupId,
        String isTitle,
        String thumbFileImagePath) {

    public PlaceResponse(Place place){
        this(place.getId(),
            place.getPlaceLng(),
            place.getPlaceLat(),
            place.getReviewRate(),
            place.getPlaceName(),
            place.getPlaceAddr1(),
            place.getPlaceAddr2(),
            place.getPlacePhone(),
            place.getPlaceAuthor(),
            place.getPlaceStart(),
            place.getPlaceClose(),
            place.getFileGroupId(),
            place.getPlaceImageList().get(0).getIsTitle(),
            place.getPlaceImageList().get(0).getThumbFileImagePath() );
    }
}
