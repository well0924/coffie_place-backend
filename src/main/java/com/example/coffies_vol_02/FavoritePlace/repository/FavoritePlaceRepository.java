package com.example.coffies_vol_02.FavoritePlace.repository;

import com.example.coffies_vol_02.FavoritePlace.domain.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace,Integer> {
}
