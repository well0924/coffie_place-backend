package com.example.coffies_vol_02.config.api.dto;

import com.example.coffies_vol_02.place.domain.Place;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("y")
    private double latitude;

    @JsonProperty("x")
    private double longitude;

    @JsonProperty("distance")
    private double distance;

    public DocumentDto(Place place) {
        this.placeName = place.getPlaceName();
        this.addressName = place.getPlaceAddr();
        this.latitude = 0.0; // DB에 위도, 경도가 없으면 기본값 설정
        this.longitude = 0.0;
        this.distance = 0.0; // 기본값 설정
    }
}
