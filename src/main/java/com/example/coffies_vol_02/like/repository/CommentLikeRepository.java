package com.example.coffies_vol_02.like.repository;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Integer> {
    Optional<CommentLike>findByMemberAndComment(Member member, Comment comment);
    Optional<Integer>countByComment(Comment comment);
}
