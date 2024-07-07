package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace,Integer>,CustomFavoritePlaceRepository {
    //위시리스트 확인
    @Query(value = "select  count(f) > 0  from FavoritePlace f where f.place.id = :placeId and f.member.userId = :memberId")
    boolean existsByPlaceIdAndMemberUserId(@Param("placeId") Integer placeId,@Param("memberId") String memberId);
}
