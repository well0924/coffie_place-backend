package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PlaceRepository extends JpaRepository<Place, Integer>, CustomPlaceRepository {

    /**
     * Haversin 공식을 적용해서 회원위치에서 가까운 가게 5개 가져오기.
     * @param lat 회원의 위도
     * @param lon 회원의 경도
     * @return List<Place>
     *
    @Query(value = "SELECT * FROM " +
            "tbl_place tp " +
            "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(tp.place_lat)) * cos(radians(tp.place_lng) - radians(:lon)) + sin(radians(:lat)) * sin(radians(tp.place_lat)))) <= 3 " +
            "ORDER BY distance LIMIT 5" ,nativeQuery = true)
    List<Place> findPlacesByMemberLocation(@Param("lat") Double lat,@Param("lon") Double lon);

    /**
     * 가게명과 일치하는 가게 출력하기.
     * @param names : kakaoApi에서 출력된 가게명들
     **/
    @Query(value = "select p from Place p where p.placeName in :names ")
    List<Place> findPlacesByName(@Param("names") List<String> names);

    Place findByPlaceName(String placeName);

}
