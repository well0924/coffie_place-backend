package com.example.coffies_vol_02.Commnet.repository;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    //댓글 목록(자유게시판)
    @Query("select c from Comment c where c.board.id = :id")
    List<Comment> findCommentsBoardId(@Param("id") Integer boardId)throws Exception;

    //댓글 목록(가게)
    @Query("select c from Comment c where c.place.id = :id")
    List<Comment>findCommentsPlaceId(@Param("id")Integer placeId)throws Exception;

    //내가 작성한 댓글
    List<Comment>findByMember(Member member, Pageable pageable);

    //댓글 평점 계산
    @Query("select avg(c.replyPoint) from Comment c where c.place.id = :id")
    Double getStarAvgByPlaceId(@Param("id") Integer placeId)throws Exception;

    //댓글 평점 갱신
    @Modifying
    @Query("update Place p set p.reviewRate = :rate where p.id = :id")
    void cafeReviewRate(@Param("rate")Double reviewRate,@Param("id")Integer placeId);
}
