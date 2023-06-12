package com.example.coffies_vol_02.place.domain.dto.request;

import javax.validation.constraints.NotBlank;

public record PlaceRequest(
        Double placeLng,
        Double placeLat,
        @NotBlank(message = "가게이름을 입력해주세요.")
        String placeName,
        @NotBlank(message = "주소를 입력해주세요.")
        String placeAddr1,
        String placeAddr2,
        @NotBlank(message = "가게 전화번호를 입력해주세요.")
        String placePhone,
        String placeAuthor,
        @NotBlank(message = "가게 시작시간을 입력해주세요.")
        String placeStart,
        @NotBlank(message = "가게 종료시간을 입력해주세요.")
        String placeClose,
        String fileGroupId,
        Double reviewRate) {


}
