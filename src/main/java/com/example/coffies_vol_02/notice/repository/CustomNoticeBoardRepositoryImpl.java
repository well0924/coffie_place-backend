package com.example.coffies_vol_02.notice.repository;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.QNoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponseDto;
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
    public Page<NoticeResponseDto> findAllList(Pageable pageable) {

        List<NoticeResponseDto>noticeList =  new ArrayList<>();

        List<NoticeBoard>result = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard)
                .from(QNoticeBoard.noticeBoard)
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),QNoticeBoard.noticeBoard.id.desc())
                .distinct()
                .fetch();

        int count = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard.count())
                .from(QNoticeBoard.noticeBoard)
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),QNoticeBoard.noticeBoard.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .size();

        for(NoticeBoard noticeBoard:result){
            NoticeResponseDto responseDto = NoticeResponseDto
                    .builder()
                    .id(noticeBoard.getId())
                    .noticeTitle(noticeBoard.getNoticeTitle())
                    .noticeWriter(noticeBoard.getNoticeWriter())
                    .noticeContents(noticeBoard.getNoticeContents())
                    .noticeGroup(noticeBoard.getNoticeGroup())
                    .fileGroupId(noticeBoard.getFileGroupId())
                    .isFixed(noticeBoard.getIsFixed())
                    .createdTime(noticeBoard.getCreatedTime())
                    .updatedTime(noticeBoard.getUpdatedTime())
                    .build();

            noticeList.add(responseDto);
        }
        return new PageImpl<>(noticeList,pageable,count);
    }

    @Override
    public Page<NoticeResponseDto> findAllSearchList(String searchVal, Pageable pageable) {

        List<NoticeResponseDto>searchResult = new ArrayList<>();

        List<NoticeBoard> result = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard)
                .from(QNoticeBoard.noticeBoard)
                .where(noticeTitleEq(searchVal).or(noticeAuthorEq(searchVal)).or(noticeContentsEq(searchVal)))
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),QNoticeBoard.noticeBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int searchCount = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard.count())
                .from(QNoticeBoard.noticeBoard)
                .where(noticeTitleEq(searchVal).or(noticeAuthorEq(searchVal)).or(noticeContentsEq(searchVal)))
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),QNoticeBoard.noticeBoard.id.desc())
                .fetch()
                .size();

        for(NoticeBoard noticeBoard:result){

            NoticeResponseDto responseDto = NoticeResponseDto
                    .builder()
                    .id(noticeBoard.getId())
                    .noticeTitle(noticeBoard.getNoticeTitle())
                    .noticeWriter(noticeBoard.getNoticeWriter())
                    .noticeContents(noticeBoard.getNoticeContents())
                    .noticeGroup(noticeBoard.getNoticeGroup())
                    .fileGroupId(noticeBoard.getFileGroupId())
                    .isFixed(noticeBoard.getIsFixed())
                    .createdTime(noticeBoard.getCreatedTime())
                    .updatedTime(noticeBoard.getUpdatedTime())
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
