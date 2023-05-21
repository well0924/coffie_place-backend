package com.example.coffies_vol_02.place.domain.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequestDto {
    private Double placeLng;
    private Double placeLat;
    @NotBlank(message = "가게이름을 입력해주세요.")
    private String placeName;
    @NotBlank(message = "주소를 입력해주세요.")
    private String placeAddr1;
    private String placeAddr2;
    @NotBlank(message = "가게 전화번호를 입력해주세요.")
    private String placePhone;
    private String placeAuthor = "well4149";
    @NotBlank(message = "가게 시작시간을 입력해주세요.")
    private String placeStart;
    @NotBlank(message = "가게 종료시간을 입력해주세요.")
    private String placeClose;
    private String fileGroupId;
    private Double reviewRate= 0.0;
}
