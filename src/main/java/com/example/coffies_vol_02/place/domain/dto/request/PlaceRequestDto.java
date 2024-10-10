package com.example.coffies_vol_02.place.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

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

    //csv파일에서 가게정보를 담는 dto
    public static PlaceRequestDto fromCsv(String[] csvLine) {
        return PlaceRequestDto.builder()
                .placeName(csvLine[1])
                .placeAddr(csvLine[2])
                .placeStart(csvLine[3])
                .placeClose(csvLine[4])
                .placePhone(csvLine[5])
                .build();
    }

    // 캐싱에 가게  정보를 캐싱하는데 필요한 dto
    public static PlaceRequestDto createPlaceRequestDtoFromCache(Map<Object, Object> cachedPlace) {
        return PlaceRequestDto.builder()
                .placeName((String) cachedPlace.get("placeName")) // 캐스팅 명확히
                .placeAddr((String) cachedPlace.get("placeAddr"))
                .placeStart((String) cachedPlace.get("placeStart"))
                .placeClose((String) cachedPlace.get("placeClose"))
                .placePhone((String) cachedPlace.get("placePhone"))
                .placeAuthor((String) cachedPlace.get("placeAuthor"))
                .build();
    }
}
