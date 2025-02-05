package com.example.coffies_vol_02.place.repository;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.QPlace;
import com.example.coffies_vol_02.place.domain.QPlaceImage;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
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
import java.util.stream.Collectors;

@Repository
public class CustomPlaceRepositoryImpl implements CustomPlaceRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QPlace place;

    private final QPlaceImage placeImage;

    public CustomPlaceRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
        this.place = QPlace.place;
        this.placeImage = QPlaceImage.placeImage;
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
     * 가게목록 무한스크롤 (no-offset)
     * @param pageable 페이징 객체
     * @return Slice<PlaceResponseDto>
     **/
    @Override
    public Slice<PlaceResponseDto> placeList(Pageable pageable, Integer placeId) {
        List<Tuple> tuples = jpaQueryFactory
                .select(place, placeImage) // Place와 PlaceImage 조회
                .from(place)
                .innerJoin(place.placeImageList, placeImage)
                .where(placeImage.isTitle.eq("Y").and(rtPlaceId(placeId))) // 키워드 조건 추가
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new)) // 정렬
                .limit(pageable.getPageSize() + 1) // 페이징
                .fetch();

        List<PlaceResponseDto> result = tuples
                .stream()
                .map(tuple -> {
                    Place p = tuple.get(place);
                    PlaceImage img = tuple.get(placeImage);

                    return PlaceResponseDto.builder()
                            .id(p.getId())
                            .placeName(p.getPlaceName())
                            .placeAddr(p.getPlaceAddr())
                            .reviewRate(p.getReviewRate())
                            .placeAuthor(p.getPlaceAuthor())
                            .placeStart(p.getPlaceStart())
                            .placeClose(p.getPlaceClose())
                            .placePhone(p.getPlacePhone())
                            .isTitle(img != null ? img.getIsTitle() : null) // 이미지 고정 여부
                            .thumbFileImagePath(img != null ? img.getThumbFileImagePath() : null) // 섬네일 이미지 경로
                            .imgPath(img != null ? img.getImgPath() : null) // 원본 이미지 경로
                            .build();
                }).collect(Collectors.toList());

        boolean hasNext = result.size() > pageable.getPageSize();

        if (hasNext) {
            result.remove(result.size() - 1); // Remove the extra element if there is a next page
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }


    private BooleanExpression rtPlaceId(Integer placeId) {
        return placeId != null ? place.id.gt(placeId) : null;
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