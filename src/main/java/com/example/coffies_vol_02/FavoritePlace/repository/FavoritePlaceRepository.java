package com.example.coffies_vol_02.FavoritePlace.repository;

import com.example.coffies_vol_02.FavoritePlace.domain.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace,Integer> {
    //위시리스트 확인
    boolean existsByPlaceIdAndMemberId(Integer placeId,Integer memberId);
    //위시리스트 삭제
    void deleteByPlaceIdAndMemberId(Integer placeId,Integer memberId);
}
