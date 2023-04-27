package com.example.coffies_vol_02.Notice.repository;

import com.example.coffies_vol_02.Board.domain.QBoard;
import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
import com.example.coffies_vol_02.Notice.domain.QNoticeBoard;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomNoticeBoardRepositoryImpl implements CustomNoticeBoardRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomNoticeBoardRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<NoticeBoardDto.BoardResponseDto> findAllSearchList(String searchVal, Pageable pageable) {
        List<NoticeBoardDto.BoardResponseDto>searchResult = new ArrayList<>();

        List<NoticeBoard> result = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard)
                .from(QNoticeBoard.noticeBoard)
                .where(noticeTitleEq(searchVal)
                        .or(noticeAuthorEq(searchVal))
                        .or(noticeContentsEq(searchVal)))
                .orderBy(QNoticeBoard.noticeBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long searchCount = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard.count())
                .from(QNoticeBoard.noticeBoard)
                .where(noticeTitleEq(searchVal)
                        .or(noticeAuthorEq(searchVal))
                        .or(noticeContentsEq(searchVal)))
                .orderBy(QNoticeBoard.noticeBoard.id.desc())
                .fetchOne();
        for(NoticeBoard noticeBoard:result){
            NoticeBoardDto.BoardResponseDto responseDto = NoticeBoardDto.BoardResponseDto
                    .builder()
                    .noticeBoard(noticeBoard)
                    .build();
            searchResult.add(responseDto);
        }
        return new PageImpl<>(searchResult,pageable,searchCount);
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

}
