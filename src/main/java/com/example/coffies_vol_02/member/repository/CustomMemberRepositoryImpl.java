package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.QMember;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Repository
public class CustomMemberRepositoryImpl implements CustomMemberRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public CustomMemberRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 회원 검색기능
     * @author : 양경빈
     * @param : String searchVal 회원 검색어 ,Pageable 목록에 사용되는 페이징 객체입니다.
     * @return : Page<MemberResponse>list 검색시 회원 목록
     **/
    @Override
    public Page<MemberResponse> findByAllSearch(SearchType searchType, String searchVal, Pageable pageable) {
        JPQLQuery<MemberResponse> query = jpaQueryFactory
                .select(Projections.constructor(MemberResponse.class, QMember.member))
                .from(QMember.member)
                .where(buildSearchPredicate(searchType, searchVal));

        List<MemberResponse> members = query
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = PageableExecutionUtils.getPage(members, pageable, query::fetchCount).getTotalElements();

        return new PageImpl<>(members, pageable, total);
    }

    /**
     * 검색 조건 빌더
     *
     * @param searchType 검색 타입
     * @param searchVal 검색어
     * @return BooleanBuilder 검색 조건
     */
    private BooleanBuilder buildSearchPredicate(SearchType searchType, String searchVal) {
        BooleanBuilder builder = new BooleanBuilder();
        switch (searchType) {
            case e -> builder.and(memberEmail(searchVal));
            case i -> builder.and(userId(searchVal));
            case n -> builder.and(memberName(searchVal));
            default -> builder.and(memberEmail(searchVal).or(memberName(searchVal).or(userId(searchVal))));
        }
        return builder;
    }

    //검색 조건 회원 이름
    BooleanBuilder memberName(String searchVal){
        return nullSafeBuilder(()->QMember.member.memberName.contains(searchVal));
    }

    //검색 조건 회원 아이디
    BooleanBuilder userId(String searchVal){
        return nullSafeBuilder(()-> QMember.member.userId.containsIgnoreCase(searchVal));
    }

    //검색 조건 회원 이메일
    BooleanBuilder memberEmail(String searchVal){
        return nullSafeBuilder(()->QMember.member.userEmail.contains(searchVal));
    }

    //null 체크
    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }

    /**
     * 동적정렬
     * @param : Sort sort 페이징객체에서 정렬을 하는
     * @return :  List<OrderSpecifier>orders 정렬된 목록 값 기본값은 오름차순
     **/
    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            String prop = order.getProperty();

            System.out.println(order);
            System.out.println("direction:"+direction);
            System.out.println("prop:"+prop);

            PathBuilder<Member> orderByExpression =  new PathBuilder<>(Member.class,QMember.member.getMetadata());
            System.out.println("orderByExpression:"+orderByExpression.get(prop));

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}
