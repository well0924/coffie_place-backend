package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Integer>,CustomBoardRepository,QuerydslPredicateExecutor {

    /**
     * 비밀 번호 확인
     **/
    @Query("select b from Board b where b.id=:id and b.passWd= :passWd")
    BoardResponse findByPassWdAndId(@Param("passWd") String password, @Param("id") Integer id);

    /**
     * 내가 작성한 글
     **/
    Page<Board> findByMember(Member member, Pageable pageable);

    /**
     * 게시글 조회수 확인
     **/
    @Query("select b.readCount from Board b where b.id = :id")
    Integer ReadCount(@Param("id") Integer id);

    /**
     * 게시글 조회수 증가.
     **/
    @Transactional
    @Modifying
    @Query("update Board b set b.readCount = :readCount where b.id = :id")
    void ReadCountUpToDB(@Param("id")Integer id, @Param("readCount")Integer readCount);

    /**
     * 게시글 다음글
     **/
    @Transactional
    @Query(nativeQuery = true,value = "select " +
            "tb.id," +
            "tb.board_title as BoardTitle," +
            "tb.board_contents as BoardContents," +
            "tb.board_author as BoardAuthor," +
            "tb.file_group_id as FileGoupId," +
            "tb.liked as Liked," +
            "tb.read_count as ReadCount," +
            "tb.created_time as CreatedTime," +
            "tb.updated_time as UpdatedTime from tbl_board tb where tb.created_time > ?1 order by tb.created_time asc limit 1")
    //Optional<BoardNextInterface> findNextBoard(LocalDateTime createdTime);
    Board findNextBoard(LocalDateTime createdTime);

    /**
     * 게시글 이전글
     **/
    @Transactional
    @Query(nativeQuery = true,value = "select " +
            "tb.id," +
            "tb.board_title as BoardTitle," +
            "tb.board_contents as BoardContents," +
            "tb.board_author as BoardAuthor," +
            "tb.file_group_id as FileGroupId," +
            "tb.liked as Liked," +
            "tb.read_count as ReadCount," +
            "tb.created_time as CreatedTime," +
            "tb.updated_time as UpdatedTime " +
            "from tbl_board tb where tb.created_time < ?1 order by tb.created_time desc limit 1")
    //Optional<BoardNextPreviousInterface> findPreviousBoard(LocalDateTime createdTime);
    Board findPreviousBoard(LocalDateTime createdTime);

    /**
     * 최근에 작성한 글목록 (5개만)
     **/
    List<BoardResponse>findTop5ByOrderByCreatedTimeDesc();
}
