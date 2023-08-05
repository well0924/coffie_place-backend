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
     * 자유게시판을 조회했을 때 첨부파일 목록을 전체 조회
     **/
    @Query("select a from Attach a where a.board.id = :id")
    List<Attach>findAttachBoard(@Param("id") Integer boardId)throws Exception;
    
    /**
     * 공지게시판 첨부파일 목록
     * 공지게시판을 조회했을 때 첨부파일 목록을 전체 조회
     **/
    @Query("select a from Attach a where a.noticeBoard.id = :id")
    List<Attach>findAttachNoticeBoard(@Param("id") Integer noticeId)throws Exception;
    
    /**
     * 첨부파일 조회
     * 게시판 상세조회 페이지에서 파일을 다운로드 할 때 개별 첨부파일을 조회
     * @param originFileName 첨부파일의 원본파일명
     **/
    Optional<Attach>findAttachByOriginFileName(String originFileName);
}