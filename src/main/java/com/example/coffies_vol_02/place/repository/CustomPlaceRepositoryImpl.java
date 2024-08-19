package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.QPlace;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Repository
public class CustomPlaceRepositoryImpl implements CustomPlaceRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QPlace place;

    public CustomPlaceRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
        this.place = QPlace.place;
    }

    /**
     * 가게 검색
     * @param pageable 페이징 객체
     * @param keyword 가게 검색에 필요한 키워드
     * @return Page<PlaceResponseDto> 페이징 객체
     **/
    @Override
    public Slice<PlaceResponseDto> placeListSearch(SearchType searchType, String keyword, Pageable pageable) {
        JPQLQuery<PlaceResponseDto> query = jpaQueryFactory
                .select(Projections.constructor(PlaceResponseDto.class, place))
                .from(place)
                .where(buildSearchPredicate(searchType, keyword));

        List<PlaceResponseDto> placeList = query
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = placeList.size() > pageable.getPageSize();
        if (hasNext) {
            placeList.remove(placeList.size() - 1); // Remove the extra element if there is a next page
        }

        return new SliceImpl<>(placeList, pageable, hasNext);
    }


    /**
     * 가게 평점 top5
     * @return Page<PlaceResponseDto>
     **/
    @Override
    public List<PlaceResponseDto> placeTop5() {
        return jpaQueryFactory
                .select(Projections.constructor(PlaceResponseDto.class, place))
                .from(place)
                .orderBy(place.reviewRate.desc())
                .limit(5)
                .fetch();
    }

    /**
     * 가게목록 무한스크롤
     * @param pageable 페이징 객체
     * @return Slice<PlaceResponseDto>
     **/
    @Override
    public Slice<PlaceResponseDto> placeList(Pageable pageable,String keyword) {
        List<Place> placeList = jpaQueryFactory
                .selectFrom(place)
                .where(placeName(keyword).or(placeAdder(keyword)))
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PlaceResponseDto> result = new ArrayList<>();
        for (Place place : placeList) {
            result.add(new PlaceResponseDto(place));
        }

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result.remove(result.size() - 1); // Remove the extra element if there is a next page
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private BooleanBuilder buildSearchPredicate(SearchType searchType, String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        switch (searchType) {
            case p -> builder.and(placeName(keyword));
            case a -> builder.and(placeAdder(keyword));
            default -> builder.and(placeName(keyword).or(placeAdder(keyword)));
        }
        return builder;
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

            PathBuilder<Place> orderByExpression =  new PathBuilder<>(QPlace.place.getType(), QPlace.place.getMetadata());

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}