package com.example.coffies_vol_02.Place.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class PlaceImageDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlaceImageRequestDto{
        private String fileGroupId;
        private String fileType;
        private String imgGroup;
        private String imgPath;
        private String thumbFilePath;
        private String thumbFileImagePath;
        private String storedName;
        private String originName;
        private String imgUploader = "well4149";
        @Builder.Default
        private String isTitle = "N";
        private List<MultipartFile>images;
    }
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceImageResponseDto{
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
    }
}
