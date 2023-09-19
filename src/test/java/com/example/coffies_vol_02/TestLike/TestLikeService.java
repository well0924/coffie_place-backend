package com.example.coffies_vol_02.TestLike;

import com.example.coffies_vol_02.Factory.*;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.like.domain.Like;
import com.example.coffies_vol_02.like.repository.CommentLikeRepository;
import com.example.coffies_vol_02.like.repository.LikeRepository;
import com.example.coffies_vol_02.like.service.LikeService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
public class TestLikeService {
    @InjectMocks
    private LikeService likeService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    Member member;
    Board board;
    Place place;
    Comment comment;
    Like like;
    CommentLike commentLike;
    private static final String LikeSuccess ="좋아요 추가";
    private static final String LikeCancel ="좋아요 취소";

    @BeforeEach
    public void init(){
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        like = LikeFactory.getLike();
        comment = CommentFactory.comment();
        place = PlaceFactory.place();
        commentLike = LikeFactory.getCommentLike();
    }

    @Test
    @DisplayName("자유게시판 좋아요 추가")
    public void boardLikeCreateTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.save(like)).willReturn(like);

        String result = likeService.createBoardLike(board.getId(),member);

        assertThat(result).isEqualTo(LikeSuccess);
    }
    @Test
    @DisplayName("자유게시판 좋아요 삭제")
    public void boardLikeCancelTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(like));
        likeRepository.delete(like);
        String result = likeService.cancelLike(board.getId(),member);

        assertThat(result).isEqualTo(LikeCancel);
    }

    @Test
    @DisplayName("자유게시판 좋아요 카운트")
    public void boardLikeCountTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(like));
        given(likeRepository.countByBoard(board)).willReturn(Optional.of(0));

        List<String> result = likeService.likeCount(board.getId(),member);

        assertThat(result.get(0)).isEqualTo("0");
    }

    @Test
    @DisplayName("가게댓글 좋아요 추가")
    public void commentLikeCreateTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentLikeRepository.save(commentLike)).willReturn(commentLike);

        String result = likeService.commentLikePlus(comment.getId(),member);

        assertThat(result).isEqualTo(LikeSuccess);
    }
    @Test
    @DisplayName("댓글 좋아요 삭제")
    public void commentLikeCancelTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentLikeRepository.findByMemberAndComment(member,comment)).willReturn(Optional.of(commentLike));

        commentLikeRepository.delete(commentLike);
        String result = likeService.commentLikeMinus(comment.getId(),member);

        assertThat(result).isEqualTo(LikeCancel);
    }

    @Test
    @DisplayName("댓글좋아요 카운트")
    public void commentLikeCountTest(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(commentLikeRepository.countByComment(comment)).willReturn(Optional.of(0));

        List<String> result = likeService.likeCommentCount(comment.getId(),member);

        assertThat(result.get(0)).isEqualTo("0");
    }
}
