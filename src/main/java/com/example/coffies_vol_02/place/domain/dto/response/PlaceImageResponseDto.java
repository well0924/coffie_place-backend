package com.example.coffies_vol_02.place.domain.dto.response;

import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceImageResponseDto {
    private Integer id;
    private String fileGroupId;
    private String fileType;
    private String imgGroup;
    private String imgPath;
    private String thumbFileImagePath;
    private String thumbFilePath;
    private String storedName;
    private String originName;
    private String imgUploader;
    private String isTitle;
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
    }
}
