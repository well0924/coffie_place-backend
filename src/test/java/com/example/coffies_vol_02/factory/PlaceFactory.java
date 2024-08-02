package com.example.coffies_vol_02.factory;

import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceImageResponseDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

public class PlaceFactory {
    public static List<PlaceImage> placeImages = new ArrayList<>();

    public static Place place(){
        return  Place
                .builder()
                .placeAddr("xxxx시 xx구")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(0.0)
                .placeName("릴렉스")
                .placeImages(placeImages)
                .build();
    }

    public static PlaceImage placeImage(){
        return PlaceImage
                .builder()
                .fileGroupId("place_ereg34593")
                .thumbFilePath("C:\\\\UploadFile\\\\coffieplace\\images\\thumb\\file_1320441223849700_thumb.jpg")
                .thumbFileImagePath("/istatic/images/coffieplace/images/thumb/1320441218420200_thumb.jpg")
                .imgPath("C:\\\\UploadFile\\\\coffieplace\\images\\1320441218420200.jpg")
                .storedName("다운로드 (1).jpg")
                .originName("1320441218420200.jpg")
                .imgUploader(MemberFactory.memberDto().getUserId())
                .imgGroup("coffieplace")
                .isTitle("1")
                .build();
    }

    public static PlaceRequestDto placeRequestDto(){
        return PlaceRequestDto
                .builder()
                .placeName(place().getPlaceName())
                .placePhone(place().getPlacePhone())
                .placeStart(place().getPlaceStart())
                .placeClose(place().getPlaceClose())
                .placeAddr(place().getPlaceAddr())
                .placeAuthor(place().getPlaceAuthor())
                .reviewRate(place().getReviewRate())
                .build();
    }

    public static PlaceResponseDto placeResponseDto(){
        return new PlaceResponseDto(
                place().getId(),
                place().getReviewRate(),
                place().getPlaceName(),
                place().getPlaceAddr(),
                place().getPlacePhone(),
                place().getPlaceAuthor(),
                place().getPlaceStart(),
                place().getPlaceClose(),
                place().getPlaceImageList().isEmpty() ? null : place().getPlaceImageList().get(0).getIsTitle(),
                place().getPlaceImageList().isEmpty() ? null : place().getPlaceImageList().get(0).getImgPath(),
                place().getPlaceImageList().isEmpty() ? null : place().getPlaceImageList().get(0).getThumbFileImagePath());
    }

    public static PlaceImageRequestDto placeImageRequestDto(){
        return PlaceImageRequestDto
                .builder()
                .images(new ArrayList<>(List.of(
                        new MockMultipartFile("test1", "가게 이미지1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                        new MockMultipartFile("test2", "가게 이미지2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                        new MockMultipartFile("test3", "가게 이미지3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes()))))
                .fileType(placeImage().getFileType())
                .imgPath(placeImage().getImgPath())
                .isTitle(placeImage().getIsTitle())
                .originName(placeImage().getOriginName())
                .storedName(placeImage().getStoredName())
                .fileGroupId(placeImage().getFileGroupId())
                .imgGroup(placeImage().getImgGroup())
                .imgUploader(placeImage().getImgUploader())
                .thumbFileImagePath(placeImage().getThumbFileImagePath())
                .thumbFilePath(placeImage().getThumbFilePath())
                .build();
    }

    public static PlaceImageResponseDto placeImageResponseDto(){
        return PlaceImageResponseDto
                .builder()
                .id(placeImage().getId())
                .imgPath(placeImage().getImgPath())
                .imgUploader(placeImage().getImgUploader())
                .isTitle(placeImage().getIsTitle())
                .storedName(placeImage().getStoredName())
                .originName(placeImage().getOriginName())
                .imgGroup(placeImage().getImgGroup())
                .thumbFileImagePath(placeImage().getThumbFileImagePath())
                .thumbFilePath(placeImage().getThumbFilePath())
                .fileGroupId(placeImage().getFileGroupId())
                .fileType(placeImage().getFileType())
                .createdTime(placeImage().getCreatedTime())
                .updatedTime(placeImage().getUpdatedTime())
                .build();
    }
}
