package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.QBoard;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.like.domain.QLike;
import com.example.coffies_vol_02.member.domain.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Log4j2
public class CustomBoardRepositoryImpl implements CustomBoardRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QMember qMember;

    private final QBoard qBoard;

    private final QLike qLike;

    public CustomBoardRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
        this.qBoard = QBoard.board;
        this.qMember = QMember.member;
        this.qLike = QLike.like;
    }

    /**
     * 게시글 목록
     * @author 양경빈
     * @param pageable 게시물 목록에서 페이징에 필요한 객체
     * @return Page<BoardResponse> 게시물 목록
     **/
    @Override
    public Page<BoardResponse> boardList(Pageable pageable) {

        List<BoardResponse> boardList = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class, qBoard))
                .from(qBoard)
                .join(qBoard.member, qMember).fetchJoin()
                .groupBy(qBoard.id)
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .distinct()
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(qBoard.id.count())
                .from(qBoard)
                .join(qBoard.member, qMember)
                .fetchOne();

        //NullPointException 예방
        totalCount = (totalCount != null) ? totalCount : 0L;

        return new PageImpl<>(boardList,pageable,totalCount);
    }

    /**
     * 게시글 검색
     * @author 양경빈
     * @param searchVal 자유게시물 목록에서 검색에 필요한 검색어
     * @param pageable 게시물 목록에서 페이징에 필요한 객체
     * @return Page<BoardResponse> 게시물 목록
     **/
    @Override
    public Page<BoardResponse> findAllSearch(SearchType searchType, String searchVal, Pageable pageable) {

        JPQLQuery<BoardResponse> list = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class, qBoard))
                .from(qBoard)
                .join(qBoard.member, qMember);

        JPQLQuery<BoardResponse> middleQuery = switch (searchType) {
            case t -> list.where(boardTitleEq(searchVal));
            case a -> list.where(boardAuthorEq(searchVal));
            case c -> list.where(boardContentsEq(searchVal));
            default -> list.where(boardTitleEq(searchVal).or(boardAuthorEq(searchVal).or(boardContentsEq(searchVal))));
        };

        return PageableExecutionUtils.getPage(
                middleQuery.orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                middleQuery::fetchCount
        );
    }

    /**
     * 게시물 단일 조회
     * @author 양경빈
     * @param boardId 게시물 번호
     * @return BoardResponse
     **/
    @Override
    public BoardResponse boardDetail(int boardId) {

        return jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class, qBoard))
                .from(qBoard)
                .join(qBoard.member, qMember).fetchJoin()
                .where(qBoard.id.eq(boardId))
                .distinct()
                .fetchOne();
    }

    /**
     * 좋아요를 한 게시물목록 조회
     * @author 양경빈
     * @param userIdx 회원 번호
     * @return List<BoardResponse>result 게시물 목록
     **/
    @Override
    public Page<BoardResponse> likedBoardDetailList(int userIdx,Pageable pageable) {
        List<BoardResponse> result = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class, qBoard.id, qBoard.boardTitle, qBoard.boardContents, qBoard.createdTime, qMember.userId))
                .from(qBoard)
                .innerJoin(qLike).on(qBoard.id.eq(qLike.board.id))
                .where(qLike.member.id.eq(userIdx))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = jpaQueryFactory
                .select(qBoard.count())
                .from(qBoard)
                .innerJoin(qLike).on(qBoard.id.eq(qLike.board.id))
                .where(qLike.member.id.eq(userIdx))
                .fetchOne();
        //NullPointException 예방
        count = (count != null) ? count : 0L;

        return new PageImpl<>(result, pageable, count);
    }

    BooleanBuilder boardContentsEq(String searchVal){
        return nullSafeBuilder(()-> QBoard.board.boardContents.contains(searchVal));
    }


    BooleanBuilder boardTitleEq(String searchVal){
        return nullSafeBuilder(()-> QBoard.board.boardTitle.contains(searchVal));
    }


    BooleanBuilder boardAuthorEq(String searchVal){
        return nullSafeBuilder(()-> QBoard.board.boardAuthor.contains(searchVal));
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

            PathBuilder<Board> orderByExpression =  new PathBuilder<>(Board.class,"board");

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}
