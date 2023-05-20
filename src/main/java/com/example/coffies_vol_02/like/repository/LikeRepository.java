package com.example.coffies_vol_02.like.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.like.domain.Like;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Integer> {
    //좋아요 중복(게시글 좋아요)
    Optional<Like> findByMemberAndBoard(Member member, Board board);
    //좋아요 카운트
    Optional<Integer>countByBoard(Board board);
}
