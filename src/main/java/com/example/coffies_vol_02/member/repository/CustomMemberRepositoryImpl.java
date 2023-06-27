package com.example.coffies_vol_02.member.repository;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.QMember;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.util.function.Supplier;

@Repository
public class CustomMemberRepositoryImpl implements CustomMemberRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomMemberRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<MemberResponse> findByAllSearch(String searchVal, Pageable pageable) {

        List<MemberResponse>responseDto = new ArrayList<>();

        List<Member>memberList = jpaQueryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(memberEmail(searchVal).or(userId(searchVal)).or(memberName(searchVal)))//검색 종류(이메일,회원아이디,회원이름)
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))//동적 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int count = jpaQueryFactory
                .select(QMember.member.count())
                .from(QMember.member)
                .where(memberEmail(searchVal).or(userId(searchVal)).or(memberName(searchVal)))//검색 종류(이메일,회원아이디,회원이름)
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))//동적 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .size();

        for(Member memberlist : memberList){
            MemberResponse dto = new MemberResponse(memberlist);
            responseDto.add(dto);
        }
        
        return new PageImpl<>(responseDto,pageable,count);
    }
    BooleanBuilder memberName(String searchVal){
        return nullSafeBuilder(()->QMember.member.memberName.contains(searchVal));
    }
    BooleanBuilder userId(String searchVal){
        return nullSafeBuilder(()-> QMember.member.userId.contains(searchVal));
    }
    BooleanBuilder memberEmail(String searchVal){
        return nullSafeBuilder(()->QMember.member.userEmail.contains(searchVal));
    }
    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
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
