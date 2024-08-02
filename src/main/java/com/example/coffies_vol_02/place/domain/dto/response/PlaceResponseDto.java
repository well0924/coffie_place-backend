package com.example.coffies_vol_02.place.domain.dto.response;

import com.example.coffies_vol_02.config.excel.ExcelColumn;
import com.example.coffies_vol_02.config.excel.ExcelFileName;
import com.example.coffies_vol_02.place.domain.Place;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@ExcelFileName(fileName = "가게 목록")
@ApiModel(description = "가게 응답 dto",value = "가게 응답 dto")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponseDto implements Serializable {
    
    @ExcelColumn(headerName = "가게 번호")
    @Schema(description = "가게 번호",type = "Integer")
    private Integer id;

    @ExcelColumn(headerName = "가게 평점")
    @Schema(description = "가게 평점",type = "Double")
    private Double reviewRate;

    @ExcelColumn(headerName = "가게 이름")
    @Schema(description = "가게 이름",type = "String")
    private String placeName;

    @ExcelColumn(headerName = "가게 주소")
    @Schema(description = "가게 주소",type = "String")
    private String placeAddr;

    @ExcelColumn(headerName = "가게 전화번호")
    @Schema(description = "가게 전화번호",type = "String")
    private String placePhone;

    @ExcelColumn(headerName = "가게 작성자")
    @Schema(description = "가게 작성자",type="String")
    private String placeAuthor;

    @ExcelColumn(headerName = "가게 영업시작 시간")
    @Schema(description = "가게 영업시작 시간",type = "String")
    private String placeStart;

    @ExcelColumn(headerName = "가게 영업 종료 시간")
    @Schema(description = "가게 영업종료시간",type = "String")
    private String placeClose;

    @Schema(description = "이미지 고정 여부",type = "String")
    private String isTitle;

    @Schema(description = "원본 이미지 경로",type = "String")
    private String imgPath;

    @Schema(description = "섬네일 이미지 경로",type = "String")
    private String thumbFileImagePath;

    @Builder
    @QueryProjection
    public PlaceResponseDto(Place place){
        this.id = place.getId();
        this.placeAuthor = place.getPlaceAuthor();
        this.placeAddr = place.getPlaceAddr();
        this.reviewRate= place.getReviewRate();
        this.placeName = place.getPlaceName();
        this.placeStart = place.getPlaceStart();
        this.placeClose = place.getPlaceClose();
        this.placePhone = place.getPlacePhone();
        this.isTitle = place.getPlaceImageList().size()== 0 ? null : place.getPlaceImageList().get(0).getIsTitle();
        this.thumbFileImagePath = place.getPlaceImageList().size() == 0 ? null : place.getPlaceImageList().get(0).getThumbFileImagePath();
    }
}
