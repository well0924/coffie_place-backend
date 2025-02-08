package com.example.coffies_vol_02.place.service;

import com.example.coffies_vol_02.config.crawling.CrawlingCacheService;
import com.example.coffies_vol_02.config.crawling.dto.PlaceImageCache;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceImageResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class PlaceImageService {

    private final PlaceImageRepository placeImageRepository;

    private final CrawlingCacheService cacheService;

    /**
     * 가게 이미지 목록
     * @param placeId 가게 번호
     * @return List<PlaceImageResponseDto>
    **/
    @Transactional(readOnly = true)
    public List<PlaceImageResponseDto>placeImageResponseDtoList(Integer placeId) throws Exception {
        // 캐시에서 메인 이미지와 서브 이미지 조회
        List<PlaceImageCache> mainImages = cacheService.getCachedImages(String.valueOf(placeId), true);
        List<PlaceImageCache> subImages = cacheService.getCachedImages(String.valueOf(placeId), false);
        log.info(mainImages);
        log.info(subImages);
        List<PlaceImageResponseDto> placeImageDtos = new ArrayList<>();

        // 메인 이미지 캐싱된 경우 반환
        if (!mainImages.isEmpty()) {
            placeImageDtos.addAll(mainImages.stream()
                    .map(imageUrl -> PlaceImageResponseDto.builder()
                            .thumbFileImagePath(imageUrl.getThumbFileImagePath())
                            .imgPath(imageUrl.getImageUrl())
                            .isTitle("Y") // 메인 이미지로 설정
                            .build())
                    .collect(Collectors.toList()));
        }

        // 서브 이미지 캐싱된 경우 반환
        if (!subImages.isEmpty()) {
            placeImageDtos.addAll(subImages.stream()
                    .map(imageUrl -> PlaceImageResponseDto.builder()
                            .thumbFileImagePath(imageUrl.getThumbFileImagePath())
                            .imgPath(imageUrl.getImageUrl())
                            .isTitle("N") // 서브 이미지로 설정
                            .build())
                    .collect(Collectors.toList()));
        }

        // 캐시에 이미지가 없는 경우 DB에서 조회 및 캐싱
        if (mainImages.isEmpty() && subImages.isEmpty()) {
            List<PlaceImage> placeImageList = placeImageRepository.findPlaceImagePlace(placeId);
            List<PlaceImageResponseDto> dbImages = getPlaceImage(placeImageList);

            // DB에서 조회한 이미지를 캐시에 저장
            dbImages.forEach(image -> {
                // DB에서 가져온 이미지를 DTO에 추가
                placeImageDtos.add(image);

                // Retrieve the PlaceImage based on the DTO's imageId
                PlaceImage placeImage = placeImageRepository.findById(image.getId()) // Assuming you have a method to fetch the PlaceImage
                        .orElseThrow(() -> new RuntimeException("Image not found for ID: " + image.getId()));

                // Redis에 캐시 (PlaceImage 객체를 직접 사용)
                cacheService.cacheImage(placeImage);  // image는 PlaceImage 타입입니다.
            });

            return dbImages;
        }
        return placeImageDtos;
    }


    /**
     * 가게 이미지 단일 조회
     * @param fileName 가게 이미지 원본 이름
     * @return PlaceImageResponseDto
     **/
    @Transactional(readOnly = true)
    public PlaceImageResponseDto getImage(String fileName){
        PlaceImage image = placeImageRepository.findByOriginName(fileName);

        PlaceImageResponseDto placeImageResponseDto
                = PlaceImageResponseDto
                .builder()
                .id(image.getId())
                .imgPath(image.getImgPath())
                .fileType(image.getFileType())
                .imgGroup(image.getImgGroup())
                .originName(image.getOriginName())
                .storedName(image.getStoredName())
                .fileGroupId(image.getFileGroupId())
                .imgUploader(image.getImgUploader())
                .isTitle(image.getIsTitle())
                .thumbFilePath(image.getThumbFilePath())
                .thumbFileImagePath(image.getThumbFileImagePath())
                .createdTime(image.getCreatedTime())
                .updatedTime(image.getUpdatedTime())
                .build();

        return placeImageResponseDto;
    }
    
    /**
     * 가게 이미지 삭제
     * @param id 가게번호
     **/
    public void deletePlaceImage(Integer id)throws Exception{
        List<PlaceImage>list = placeImageRepository.findPlaceImagePlace(id);
        placeImageRepository.deleteAll(list);
    }

    private List<PlaceImageResponseDto>getPlaceImage(List<PlaceImage>placeImageList){
        List<PlaceImageResponseDto>result = new ArrayList<>();

        for(PlaceImage image:placeImageList){
            PlaceImageResponseDto placeImageResponseDto
                    = PlaceImageResponseDto
                    .builder()
                    .id(image.getId())
                    .imgPath(image.getImgPath())
                    .fileType(image.getFileType())
                    .imgGroup(image.getImgGroup())
                    .originName(image.getOriginName())
                    .storedName(image.getStoredName())
                    .fileGroupId(image.getFileGroupId())
                    .imgUploader(image.getImgUploader())
                    .isTitle(image.getIsTitle())
                    .thumbFilePath(image.getThumbFilePath())
                    .thumbFileImagePath(image.getThumbFileImagePath())
                    .createdTime(image.getCreatedTime())
                    .updatedTime(image.getUpdatedTime())
                    .build();

            result.add(placeImageResponseDto);
        }
        return result;
    }
}
