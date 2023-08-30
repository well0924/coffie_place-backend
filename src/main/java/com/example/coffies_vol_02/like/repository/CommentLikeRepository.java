package com.example.coffies_vol_02.like.repository;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
