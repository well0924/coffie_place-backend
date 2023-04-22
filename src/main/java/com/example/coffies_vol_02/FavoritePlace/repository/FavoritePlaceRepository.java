package com.example.coffies_vol_02.FavoritePlace.repository;

import com.example.coffies_vol_02.FavoritePlace.domain.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace,Integer> {
    //위시리스트 확인
    boolean existsByPlaceIdAndMemberId(Integer placeId,Integer memberId);
    void deleteByPlaceIdAndMemberId(Integer placeId,Integer memberId);
}
