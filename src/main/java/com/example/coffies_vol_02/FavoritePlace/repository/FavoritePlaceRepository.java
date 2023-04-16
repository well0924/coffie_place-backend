package com.example.coffies_vol_02.FavoritePlace.repository;

import com.example.coffies_vol_02.FavoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace,Integer> {
    //위시리스트
    Optional<FavoritePlace>findByPlaceAndMember(Place place, Member member);
}
