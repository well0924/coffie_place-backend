package com.example.coffies_vol_02.Place.service;

import com.example.coffies_vol_02.Place.domain.PlaceImage;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.Place.repository.PlaceImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PlaceImageService {
    private final PlaceImageRepository placeImageRepository;

    /*
    * 가게 이미지 목록
    */
    @Transactional(readOnly = true)
    public List<PlaceImageDto.PlaceImageResponseDto>placeImageResponseDtoList(Integer placeId) throws Exception {
        List<PlaceImage> placeImageList = placeImageRepository.findPlaceImagePlace(placeId);
        return getPlaceImage(placeImageList);
    }

    @Transactional(readOnly = true)
    public PlaceImageDto.PlaceImageResponseDto getImage(String fileName){
        PlaceImage image = placeImageRepository.findByOriginName(fileName);

        PlaceImageDto.PlaceImageResponseDto placeImageResponseDto
                = PlaceImageDto.PlaceImageResponseDto
                .builder()
                .placeImage(image)
                .build();

        return placeImageResponseDto;
    }
    public void deletePlaceImage(Integer id)throws Exception{
        List<PlaceImage>list = placeImageRepository.findPlaceImagePlace(id);
        for(PlaceImage image: list){
            placeImageRepository.delete(image);
        }
    }
    private List<PlaceImageDto.PlaceImageResponseDto>getPlaceImage(List<PlaceImage>placeImageList){
        List<PlaceImageDto.PlaceImageResponseDto>result = new ArrayList<>();

        for(PlaceImage image:placeImageList){
            PlaceImageDto.PlaceImageResponseDto placeImageResponseDto
                    = PlaceImageDto.PlaceImageResponseDto
                    .builder()
                    .placeImage(image)
                    .build();

            result.add(placeImageResponseDto);
        }
        return result;
    }
}
