package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceImageRepository extends JpaRepository<PlaceImage,Integer>,CustomPlaceRepository{

    /**
     * 가게 이미지 목록
     * @param placeId 가게번호
     **/
    @Query("select p from PlaceImage p where p.place.id = :id")
    List<PlaceImage>findPlaceImagePlace(@Param("id")Integer placeId)throws Exception;
    
    /**
     * 가게 이미지 조회(단일)
     * @param originName 저장된 원본 파일명
     **/
    PlaceImage findByOriginName(String originName);

    void deleteByPlace(Place place);

    boolean existsByPlaceAndStoredName(Place place, String StoredFileName);

    PlaceImage findByPlaceAndStoredName(Place place, String StoredFileName);

}
