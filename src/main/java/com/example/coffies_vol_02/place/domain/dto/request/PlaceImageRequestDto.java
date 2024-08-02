package com.example.coffies_vol_02.place.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@ApiModel(value = "가게 이미지 요청 dto",description = "가게이미지 요청 dto")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceImageRequestDto {

    @Schema(name = "fileGroupId",type = "String")
    private String fileGroupId;

    @Schema(name = "fileType",type = "String")
    private String fileType;

    @Schema(name = "imgGroup",type = "String")
    private String imgGroup;

    @Schema(name = "imgPath",type = "String")
    private String imgPath;

    @Schema(name = "thumbFilePath",type = "String")
    private String thumbFilePath;

    @Schema(name = "thumbFileImagePath",type = "String")
    private String thumbFileImagePath;

    @Schema(name = "storedName",type = "String")
    private String storedName;

    @Schema(name = "originName",type = "String")
    private String originName;

    @Schema(name = "imgUploader",type = "String")
    private String imgUploader;

    @Schema(name = "isTitle",type = "String")
    @Builder.Default
    private String isTitle = "N";

    @Schema(name = "images",type = "List")
    private List<MultipartFile> images;
}
