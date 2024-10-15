package com.example.coffies_vol_02.config.crawling.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceImageCache implements Serializable {
    private String imageId; // 이미지 ID
    private String placeId; // 가게 ID
    private String imageUrl; // 이미지 URL
    private String fileGroupId; // 파일 그룹 ID
    private String fileType; // 파일 타입
    private String imgGroup; // 이미지 그룹
    private String thumbFilePath; // 썸네일 파일 경로
    private String thumbFileImagePath; // 썸네일 이미지 경로
    private String storedName; // 저장된 이름
    private String originName; // 원본 이름
    private String imgUploader; // 업로더
    private String isTitle; // 제목 여부
}
