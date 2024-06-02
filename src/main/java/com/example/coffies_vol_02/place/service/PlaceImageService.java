package com.example.coffies_vol_02.place.service;

import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceImageResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class PlaceImageService {

    private final PlaceImageRepository placeImageRepository;

    /*
    * 가게 이미지 목록
    */
    @Transactional(readOnly = true)
    public List<PlaceImageResponseDto>placeImageResponseDtoList(Integer placeId) throws Exception {
        List<PlaceImage> placeImageList = placeImageRepository.findPlaceImagePlace(placeId);
        return getPlaceImage(placeImageList);
    }

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
