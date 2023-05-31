package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.favoritePlace.domain.QFavoritePlace;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponse;
import com.example.coffies_vol_02.member.domain.QMember;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.QPlace;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomFavoritePlaceRepositoryImpl implements CustomFavoritePlaceRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomFavoritePlaceRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }
    
    //위시리스트 목록
    @Override
    public Page<FavoritePlaceResponse> favoritePlaceWishList(Pageable pageable, String userId) {
        List<FavoritePlaceResponse>favoritePlaceDtoList = new ArrayList<>();

        List<FavoritePlace> wishList = jpaQueryFactory
                .select(QFavoritePlace.favoritePlace)
                .from(QFavoritePlace.favoritePlace)
                .join(QFavoritePlace.favoritePlace.member, QMember.member).fetchJoin()
                .join(QFavoritePlace.favoritePlace.place, QPlace.place).fetchJoin()
                .where(QFavoritePlace.favoritePlace.member.userId.eq(userId))
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .distinct()
                .fetch();

        for(FavoritePlace favoritePlace : wishList){
            FavoritePlaceResponse result = new FavoritePlaceResponse(favoritePlace);
            favoritePlaceDtoList.add(result);
        }

        int wishListSize = jpaQueryFactory
                .select(QFavoritePlace.favoritePlace)
                .from(QFavoritePlace.favoritePlace)
                .where(QFavoritePlace.favoritePlace.member.userId.eq(userId))
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch()
                .size();

        return new PageImpl<>(favoritePlaceDtoList,pageable,wishListSize);
    }

    //동적정렬
    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            String prop = order.getProperty();

            System.out.println(order);
            System.out.println("direction:"+direction);
            System.out.println("prop:"+prop);

            PathBuilder<Place> orderByExpression =  new PathBuilder<>(QPlace.place.getType(),QPlace.place.getMetadata());
            System.out.println("orderByExpression:"+orderByExpression.get(prop));

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}
