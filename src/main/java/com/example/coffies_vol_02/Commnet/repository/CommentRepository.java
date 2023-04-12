package com.example.coffies_vol_02.Commnet.repository;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<Comment>findByMember(Member member, Pageable pageable);
}
