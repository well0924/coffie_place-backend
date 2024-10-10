package com.example.coffies_vol_02.place.domain.dto.response;

import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel(description = "가게 이미지 응답 dto")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceImageResponseDto {

    @ApiModelProperty(name = "가게 이미지 번호",dataType = "Integer")
    private Integer id;

    @ApiModelProperty(name = "파일 그룹 아이디",dataType = "String")
    private String fileGroupId;

    @ApiModelProperty(name = "파일 타입",dataType = "String",example = "coffies,board")
    private String fileType;

    @ApiModelProperty(name = "이미지 그룹",dataType = "String")
    private String imgGroup;

    @ApiModelProperty(name = "이미지 경로(로컬)",dataType = "String")
    private String imgPath;

    @ApiModelProperty(name = "섬네일 이미지 경로(외부경로)",dataType = "String")
    private String thumbFileImagePath;

    @ApiModelProperty(name = "섬네일 이미지 경로(로컬)",dataType = "String")
    private String thumbFilePath;

    @ApiModelProperty(name = "저장된 파일이름",dataType = "String")
    private String storedName;

    @ApiModelProperty(name = "원본 파일이름",dataType = "String")
    private String originName;

    @ApiModelProperty(name = "이미지 업로더",dataType = "String")
    private String imgUploader;

    @ApiModelProperty(name = "메인 이미지 여부",dataType = "String")
    private String isTitle;

    @ApiModelProperty(name = "가게 번호",dataType = "Integer")
    private Integer placeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedTime;

    @Builder
    public PlaceImageResponseDto(PlaceImage placeImage){
        this.id = placeImage.getId();
        this.fileGroupId = placeImage.getFileGroupId();
        this.fileType = placeImage.getFileType();
        this.imgGroup = placeImage.getImgGroup();
        this.imgPath = placeImage.getImgPath();
        this.thumbFileImagePath = placeImage.getThumbFileImagePath();
        this.thumbFilePath = placeImage.getThumbFilePath();
        this.originName = placeImage.getOriginName();
        this.storedName = placeImage.getStoredName();
        this.imgUploader = placeImage.getImgUploader();
        this.isTitle = placeImage.getIsTitle();
        this.placeId = placeImage.getPlace().getId();
    }
}
