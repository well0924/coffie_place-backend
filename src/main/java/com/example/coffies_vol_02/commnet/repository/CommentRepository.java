package com.example.coffies_vol_02.commnet.repository;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {

    /**
     * 댓글 목록(자유게시판)
     **/
    List<Comment> findByBoardId(Integer boardId)throws Exception;

    /**
     * 댓글 목록(가게)
     **/
    List<Comment>findByPlaceId(Integer placeId)throws Exception;

    /**
     * 내가 작성한 댓글
     **/
    List<Comment>findByMember(Member member, Pageable pageable);

    /**
     * 최근에 작성한 댓글 5개(가게,자유게시판)
     **/
    List<placeCommentResponseDto>findTop5ByOrderByCreatedTimeDesc();
}
