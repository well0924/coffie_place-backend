package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Integer>, CustomPlaceRepository {

    @Query(value = "SELECT *, (6371 * acos(cos(radians(:lat)) * cos(radians(tp.place_lat)) * cos(radians(tp.place_lng) - radians(:lon)) + sin(radians(:lat)) * sin(radians(tp.place_lat)))) AS distance FROM tbl_place tp ORDER BY distance LIMIT 5", nativeQuery = true)
    List<Place> findPlaceByLatLng(Double lat, Double lon);
}
