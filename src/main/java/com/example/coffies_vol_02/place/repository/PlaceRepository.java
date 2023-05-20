package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Integer>,CustomPlaceRepository {
}
