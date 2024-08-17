package com.example.coffies_vol_02.notice.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.QNoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QList;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomNoticeBoardRepositoryImpl implements CustomNoticeBoardRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomNoticeBoardRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 공지 게시글 목록
     * @author 양경빈
     * @param pageable 페이징 객체
     * @return Page<NoticeResponse>
     **/
    @Override
    public Page<NoticeResponse> findAllList(Pageable pageable) {

        List<NoticeResponse>noticeList =  new ArrayList<>();

        List<NoticeBoard>result = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard)
                .from(QNoticeBoard.noticeBoard)
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),
                        QNoticeBoard.noticeBoard.id.desc())
                .distinct()
                .fetch();

        Long count = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard.id.count())
                .from(QNoticeBoard.noticeBoard).fetchOne();

        for(NoticeBoard noticeBoard:result){
            NoticeResponse responseDto = new NoticeResponse(noticeBoard);
            noticeList.add(responseDto);
        }
        return new PageImpl<>(noticeList,pageable,count);
    }

    /**
     * 공지게시글 검색
     * @author 양경빈
     * @param searchVal 공지게시글 검색어
     * @param pageable 페이징 객체
     * @return Page<NoticeResponse>searchResult
     **/
    @Override
    public Page<NoticeResponse> findAllSearchList(SearchType searchType, String searchVal, Pageable pageable) {

        JPQLQuery<NoticeResponse> list = jpaQueryFactory
                .select(Projections.constructor(NoticeResponse.class, QNoticeBoard.noticeBoard))
                .from(QNoticeBoard.noticeBoard);

        JPQLQuery<NoticeResponse> middleQuery = switch (searchType){
            case t -> list.where(noticeTitleEq(searchVal));
            case a -> list.where(noticeAuthorEq(searchVal));
            case c -> list.where(noticeContentsEq(searchVal));
            //case c, w, t, p, a -> null;
            default -> list.where(noticeTitleEq(searchVal).or(noticeAuthorEq(searchVal).or(noticeContentsEq(searchVal))));
        };
        return PageableExecutionUtils.getPage(middleQuery
                        .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch(),pageable,middleQuery::fetchCount);
    }

    BooleanBuilder noticeContentsEq(String searchVal){
        return nullSafeBuilder(()-> QNoticeBoard.noticeBoard.noticeContents.contains(searchVal));
    }

    BooleanBuilder noticeTitleEq(String searchVal){
        return nullSafeBuilder(()-> QNoticeBoard.noticeBoard.noticeTitle.contains(searchVal));
    }

    BooleanBuilder noticeAuthorEq(String searchVal){
        return nullSafeBuilder(()-> QNoticeBoard.noticeBoard.noticeWriter.contains(searchVal));
    }

    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }

    /**
     * 동적정렬
     * @param sort 페이징객체에서 정렬을 하는 객체
     * @return List<OrderSpecifier>orders 정렬된 목록 값 기본값은 오름차순
     **/
    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            String prop = order.getProperty();

            PathBuilder<NoticeBoard> orderByExpression =  new PathBuilder<>(NoticeBoard.class,"noticeBoard");

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}
