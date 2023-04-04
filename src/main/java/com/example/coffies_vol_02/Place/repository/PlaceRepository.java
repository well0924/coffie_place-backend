package com.example.coffies_vol_02.Place.repository;

import com.example.coffies_vol_02.Place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Integer> {
}
