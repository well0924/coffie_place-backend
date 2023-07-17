package com.example.coffies_vol_02.notice.repository;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.QNoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
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

        int count = jpaQueryFactory
                .select(QNoticeBoard.noticeBoard.count())
                .from(QNoticeBoard.noticeBoard)
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),
                        QNoticeBoard.noticeBoard.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .size();

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

        List<NoticeResponse>searchResult = new ArrayList<>();

        List<NoticeBoard> result = noticeBoardList(searchType,searchVal,pageable);

        int searchCount = searchResultCount(searchType,searchVal);

        for(NoticeBoard noticeBoard:result){
            NoticeResponse responseDto = new NoticeResponse(noticeBoard);
            searchResult.add(responseDto);
        }

        return new PageImpl<>(searchResult,pageable,searchCount);
    }

    /**
     *  공지게시글 검색 목록
     * @param searchVal 검색어
     * @param pageable 페이징 객체
     * @return List<NoticeBoard>
     **/
    List<NoticeBoard>noticeBoardList(SearchType searchType,String searchVal,Pageable pageable){
        return jpaQueryFactory
                .select(QNoticeBoard.noticeBoard)
                .from(QNoticeBoard.noticeBoard)
                .where(switch (searchType){
                    case t -> noticeTitleEq(searchVal);
                    case c -> noticeContentsEq(searchVal);
                    case w -> noticeAuthorEq(searchVal);
                    case a,p -> null;
                    case all -> noticeTitleEq(searchVal).and(noticeTitleEq(searchVal).and(noticeContentsEq(searchVal)));
                })
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),
                        QNoticeBoard.noticeBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 공지게시글 검색 목록 수
     * @param searchVal 검색어
     * @return int 검색 게시글 갯수
     **/
    int searchResultCount(SearchType searchType,String searchVal){
        return jpaQueryFactory
                .select(QNoticeBoard.noticeBoard.count())
                .from(QNoticeBoard.noticeBoard)
                .where(switch (searchType){
                    case t -> noticeTitleEq(searchVal);
                    case c -> noticeContentsEq(searchVal);
                    case w -> noticeAuthorEq(searchVal);
                    case a,p -> null;
                    case all -> noticeTitleEq(searchVal).and(noticeTitleEq(searchVal).and(noticeContentsEq(searchVal)));
                })
                .orderBy(QNoticeBoard.noticeBoard.isFixed.desc(),
                        QNoticeBoard.noticeBoard.id.desc())
                .fetch()
                .size();

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
