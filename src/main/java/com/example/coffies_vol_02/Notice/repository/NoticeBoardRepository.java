package com.example.coffies_vol_02.Notice.repository;

import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NoticeBoardRepository extends JpaRepository<NoticeBoard,Integer>,CustomNoticeBoardRepository,QuerydslPredicateExecutor {
    @Query("select n from NoticeBoard n order by n.isFixed desc ,n.id desc")
    Page<NoticeBoard>findAll(Pageable pageable);
}
