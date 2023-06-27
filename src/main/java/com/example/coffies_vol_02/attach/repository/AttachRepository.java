package com.example.coffies_vol_02.attach.repository;

import com.example.coffies_vol_02.attach.domain.Attach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AttachRepository extends JpaRepository<Attach,Integer> {

    /**
     * 자유게시판 첨부파일 목록
     **/
    @Query("select a from Attach a where a.board.id = :id")
    List<Attach>findAttachBoard(@Param("id") Integer boardId)throws Exception;
    
    /**
     * 공지게시판 첨부파일 목록
     **/
    @Query("select a from Attach a where a.noticeBoard.id = :id")
    List<Attach>findAttachNoticeBoard(@Param("id") Integer noticeId)throws Exception;
    
    /**
     * 자유게시판 첨부파일 조회
     **/
    Optional<Attach>findAttachByOriginFileName(String originFileName);
}