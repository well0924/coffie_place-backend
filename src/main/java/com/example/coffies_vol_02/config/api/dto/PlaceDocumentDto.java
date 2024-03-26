package com.example.coffies_vol_02.config.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDocumentDto {
    private String id;
    @JsonProperty("place_name")
    private String placeName;

    private String phone;
    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("road_address_name")
    private String roadAddressName;


    @JsonProperty("x")
    private double longitude;
    @JsonProperty("y")
    private double latitude;

    @JsonProperty("place_url")
    private String placeUrl;
    @JsonProperty("distance")
    private double distance;
}
