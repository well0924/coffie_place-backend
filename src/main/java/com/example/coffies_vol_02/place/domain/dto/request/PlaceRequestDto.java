package com.example.coffies_vol_02.place.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "가게 요청Dto",description = "가게 요청에 필요한 dto")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequestDto {
    @Schema
    private Double placeLng;
    @Schema
    private Double placeLat;
    @Schema
    @NotBlank(message = "가게이름을 입력해주세요.")
    private String placeName;
    @Schema
    @NotBlank(message = "주소를 입력해주세요.")
    private String placeAddr1;
    @Schema
    private String placeAddr2;
    @Schema
    @NotBlank(message = "가게 전화번호를 입력해주세요.")
    private String placePhone;
    @Schema
    private String placeAuthor = "well4149";
    @Schema
    @NotBlank(message = "가게 시작시간을 입력해주세요.")
    private String placeStart;
    @Schema
    @NotBlank(message = "가게 종료시간을 입력해주세요.")
    private String placeClose;
    @Schema
    private String fileGroupId;
    @Schema
    private Double reviewRate= 0.0;
}
