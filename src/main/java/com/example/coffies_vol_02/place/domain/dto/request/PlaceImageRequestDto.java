package com.example.coffies_vol_02.place.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@ApiModel
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceImageRequestDto {
    @Schema
    private String fileGroupId;
    @Schema
    private String fileType;
    @Schema
    private String imgGroup;
    @Schema
    private String imgPath;
    @Schema
    private String thumbFilePath;
    @Schema
    private String thumbFileImagePath;
    @Schema
    private String storedName;
    @Schema
    private String originName;
    @Schema
    private String imgUploader;
    @Schema
    @Builder.Default
    private String isTitle = "N";
    @Schema
    private List<MultipartFile> images;
}
