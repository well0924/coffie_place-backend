package com.example.coffies_vol_02.Place.repository;

import com.example.coffies_vol_02.Place.domain.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceImageRepository extends JpaRepository<PlaceImage,Integer> {
    @Query("select p from PlaceImage p where p.place.id = :id")
    List<PlaceImage>findPlaceImagePlace(@Param("id")Integer placeId)throws Exception;
    PlaceImage findByOriginName(String originName);
}
