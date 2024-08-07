package com.example.coffies_vol_02.like.repository;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Integer> {
    
    /**
     * 댓글 좋아요 확인 
     * @author 양경빈
     * @param member 회원 객체
     * @param comment 댓글 객체
     **/
    Optional<CommentLike>findByMemberAndComment(Member member, Comment comment);
    
    /**
     * 댓글 좋아요 수 
     * @author 양경빈
     * @param comment 댓글 객체
     **/
    Optional<Integer>countByComment(Comment comment);

    @Query(value = "select c from CommentLike c where c.comment.id = :commentId")
    CommentLike findByCommentId(@Param("commentId") Integer commentId);

    @Modifying
    @Query(value = "delete from CommentLike c where c.id = :id")
    void deleteByCommentLike(@Param("id") Integer likeId);
}
