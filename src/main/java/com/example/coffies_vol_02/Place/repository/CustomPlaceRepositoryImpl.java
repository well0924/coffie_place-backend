package com.example.coffies_vol_02.Place.repository;

import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.QMember;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.domain.QPlace;
import com.example.coffies_vol_02.Place.domain.QPlaceImage;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;

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
        int count = jpaQueryFactory
                .select(QPlace.place.count())
                .from(QPlace.place)
                .where(placeName(keyword).or(placeAdder(keyword)))
                .orderBy(QPlace.place.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .size();

        return new PageImpl<>(placeList,pageable,count);
    }

    //가게 평점 top5
    @Override
    public Page<PlaceDto.PlaceResponseDto> placeTop5(Pageable pageable) {
        List<PlaceDto.PlaceResponseDto>result = new ArrayList<>();

        List<Place>list = jpaQueryFactory
                .select(QPlace.place)
                .from(QPlace.place)
                .orderBy(QPlace.place.reviewRate.desc())
                .limit(5L)
                .offset(pageable.getOffset())
                .fetch();

        for(Place place : list){

            PlaceDto.PlaceResponseDto dto = PlaceDto.PlaceResponseDto
                    .builder()
                    .place(place)
                    .build();

            result.add(dto);
        }

        int size = jpaQueryFactory
                .select(QPlace.place.count())
                .from(QPlace.place)
                .orderBy(QPlace.place.reviewRate.desc())
                .limit(5)
                .offset(pageable.getOffset())
                .fetch()
                .size();

        return new PageImpl<>(result,pageable,size);
    }

    //가게목록 무한스크롤
    @Override
    public Slice<PlaceDto.PlaceResponseDto> placeList(Pageable pageable,String keyword) {
        List<Place>placelist = jpaQueryFactory
                .select(QPlace.place)
                .from(QPlace.place)
                .where(placeName(keyword).or(placeAdder(keyword)))
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize()+1)
                .fetch();//limit보다 한개를 더 들고 온다.

        //total page개수를 가져오지 않는다는 점이 page와 다른점
        List<PlaceDto.PlaceResponseDto>result = new ArrayList<>();

        for(Place place : placelist){

            PlaceDto.PlaceResponseDto placeResponseDto = PlaceDto.PlaceResponseDto
                    .builder()
                    .place(place)
                    .build();

            result.add(placeResponseDto);
        }
        boolean hasNext= false;

        if (result.size() > pageable.getPageSize()) {
            result.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(result,pageable,hasNext);
    }

    //가게 이름 조건
    BooleanBuilder placeName(String keyword){
        return new BooleanBuilder(nullSafeBuilder(()-> QPlace.place.placeName.contains(keyword)));
    }
    //가게 주소 조건
    BooleanBuilder placeAdder(String keyword){
        return new BooleanBuilder(nullSafeBuilder(()->QPlace.place.placeName.contains(keyword)));
    }

    //null 여부를 체크
    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }

    //동적 정렬(평점,가게 이름)
    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            String prop = order.getProperty();

            System.out.println(order);
            System.out.println("direction:"+direction);
            System.out.println("prop:"+prop);

            PathBuilder<Place> orderByExpression =  new PathBuilder<>(QPlace.place.getType(), QPlace.place.getMetadata());
            System.out.println("orderByExpression:"+orderByExpression.get(prop));

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}