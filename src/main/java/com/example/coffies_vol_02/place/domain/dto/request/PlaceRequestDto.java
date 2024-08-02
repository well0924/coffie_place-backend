package com.example.coffies_vol_02.place.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "가게 요청Dto",description = "가게 요청에 필요한 dto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequestDto {

    @Schema(type = "String",description = "가게명")
    @NotBlank(message = "가게이름을 입력해주세요.")
    private String placeName;

    @Schema(type = "String",description = "가게 주소")
    @NotBlank(message = "주소를 입력해주세요.")
    private String placeAddr;

    @Schema
    @NotBlank(message = "가게 전화번호를 입력해주세요.")
    private String placePhone;

    @Schema
    @Builder.Default
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
    @Builder.Default
    private Double reviewRate= 0.0;
}
