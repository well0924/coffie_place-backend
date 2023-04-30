package com.example.coffies_vol_02.Place.repository;

import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.domain.QPlace;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomPlaceRepositoryImpl implements CustomPlaceRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomPlaceRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    //가게 검색
    @Override
    public Page<PlaceDto.PlaceResponseDto> placeListSearch(String keyword, Pageable pageable) {
        List<PlaceDto.PlaceResponseDto>placeList= new ArrayList<>();

        List<Place>result = jpaQueryFactory
                .select(QPlace.place)
                .from(QPlace.place)
                .where(placeName(keyword).or(placeAdder(keyword)))
                .orderBy(QPlace.place.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for(Place place : result){
            PlaceDto.PlaceResponseDto dto = PlaceDto.PlaceResponseDto
                    .builder()
                    .place(place)
                    .build();
            placeList.add(dto);
        }

        //가게 검색 결과수
        Long count = jpaQueryFactory
                .select(QPlace.place.count())
                .from(QPlace.place)
                .where(placeName(keyword).or(placeAdder(keyword)))
                .orderBy(QPlace.place.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        return new PageImpl<>(placeList,pageable,count);
    }

    //가게 평점 top5
    @Override
    public Page<PlaceDto.PlaceResponseDto> placeTop5(Pageable pageable) {
        List<Place>list = jpaQueryFactory
                .select(QPlace.place)
                .from(QPlace.place)
                .where(QPlace.place.placeImageList.get(1).isTitle.eq(String.valueOf(1)))
                .limit(5L)
                .fetch();
        return null;
    }

    //가게 이름 조건
    BooleanBuilder placeName(String keyword){
        return new BooleanBuilder(nullSafeBuilder(()-> QPlace.place.placeName.contains(keyword)));
    }
    //가게 주소 조건
    BooleanBuilder placeAdder(String keyword){
        return new BooleanBuilder(nullSafeBuilder(()->QPlace.place.placeName.contains(keyword)));
    }
    //평점 정렬
    BooleanBuilder placeReviewRate(double reviewRate){
        return new BooleanBuilder(nullSafeBuilder(()->QPlace.place.reviewRate.eq(reviewRate)));
    }
    //null 여부를 체크
    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
}
