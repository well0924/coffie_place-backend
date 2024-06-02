package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Integer>, CustomPlaceRepository {

    /**
     * Haversin 공식을 적용해서 회원위치에서 가까운 가게 5개 가져오기.
     * @param lat 회원의 위도
     * @param lon 회원의 경도
     * @return List<Place>
     **/
    /*@Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(tp.place_lat)) * cos(radians(tp.place_lng) - radians(:lon)) + sin(radians(:lat)) * sin(radians(tp.place_lat)))) AS distance " +
            "FROM tbl_place tp " +
            "ORDER BY distance LIMIT 5",
            nativeQuery = true)*/
    @Query(value = "SELECT * FROM " +
            "tbl_place tp " +
            "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(tp.place_lat)) * cos(radians(tp.place_lng) - radians(:lon)) + sin(radians(:lat)) * sin(radians(tp.place_lat)))) <= 3 " +
            "ORDER BY distance LIMIT 5" ,nativeQuery = true)
    List<Place> findPlaceByLatLng(Double lat, Double lon);
}
