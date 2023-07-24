package com.example.coffies_vol_02.Factory;

import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;

import java.util.ArrayList;
import java.util.List;

public class PlaceFactory {
    public static List<PlaceImage> placeImages = new ArrayList<>();

    public static Place place(){
        return  Place
                .builder()
                .id(1)
                .placeLng(123.3443)
                .placeLat(23.34322)
                .placeAddr1("xxxx시 xx구")
                .placeAddr2("ㅁㄴㅇㄹ")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(0.0)
                .fileGroupId("place_fre353")
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
}
