package com.example.coffies_vol_02.place.domain.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceImageRequestDto {
    private String fileGroupId;
    private String fileType;
    private String imgGroup;
    private String imgPath;
    private String thumbFilePath;
    private String thumbFileImagePath;
    private String storedName;
    private String originName;
    private String imgUploader;
    @Builder.Default
    private String isTitle = "N";
    private List<MultipartFile> images;
}
