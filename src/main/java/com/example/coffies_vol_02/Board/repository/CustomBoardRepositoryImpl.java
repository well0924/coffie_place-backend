package com.example.coffies_vol_02.Board.repository;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.QBoard;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Repository
public class CustomBoardRepositoryImpl implements CustomBoardRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomBoardRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }
    //게시물 검색
    @Override
    public Page<BoardDto.BoardResponseDto> findAllSearch(String searchVal, Pageable pageable) {
        List<Board>result= jpaQueryFactory
                .selectFrom(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .where(boardContentsEq(searchVal)
                        .or(boardAuthorEq(searchVal))
                        .or(boardTitleEq(searchVal)))
                .fetch();
        List<BoardDto.BoardResponseDto>boardSearchResult = new ArrayList<>();
        
        Long resultCount = jpaQueryFactory
                .select(QBoard.board.count())
                .from(QBoard.board)
                .where(boardAuthorEq(searchVal).or(boardContentsEq(searchVal)).or(boardTitleEq(searchVal)))
                .fetchOne();
        for(Board board:result){
            BoardDto.BoardResponseDto responseDto = BoardDto.BoardResponseDto
                    .builder()
                    .id(board.getId())
                    .boardTitle(board.getBoardTitle())
                    .boardContents(board.getBoardContents())
                    .passWd(board.getPassWd())
                    .fileGroupId(board.getFileGroupId())
                    .readCount(board.getReadCount())
                    .createdTime(board.getCreatedTime())
                    .updatedTime(board.getUpdatedTime())
                    .build();
            boardSearchResult.add(responseDto);
        }
        return new PageImpl<>(boardSearchResult,pageable,resultCount);
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
}
