package com.example.coffies_vol_02.TestLike;

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
import com.example.coffies_vol_02.member.domain.Role;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;
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
        member = memberDto();
        board = getBoard();
        like = getLike();
        comment = comment();
        place = getPlace();
        commentLike = getCommentLike();
    }

    @Test
    @DisplayName("좋아요 추가")
    public void boardLikeCreateTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.save(like)).willReturn(like);

        String result = likeService.createBoardLike(board.getId(),member);

        assertThat(result).isEqualTo(LikeSuccess);
    }
    @Test
    @DisplayName("좋아요 삭제")
    public void boardLikeCancelTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(getLike()));
        likeRepository.delete(like);
        String result = likeService.cancelLike(board.getId(),member);

        assertThat(result).isEqualTo(LikeCancel);
    }

    @Test
    @DisplayName("좋아요 카운트")
    public void boardLikeCountTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(getLike()));
        given(likeRepository.countByBoard(board)).willReturn(Optional.of(0));

        List<String> result = likeService.likeCount(board.getId(),member);

        assertThat(result.get(0)).isEqualTo("0");
    }

    @Test
    @DisplayName("댓글 좋아요 추가")
    public void commentLikeCreateTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        given(commentLikeRepository.save(commentLike)).willReturn(commentLike);

        String result = likeService.commentLikePlus(anyInt(),comment.getId(),member);

        assertThat(result).isEqualTo(LikeSuccess);
    }
    @Test
    @DisplayName("댓글 좋아요 삭제")
    public void commentLikeCancelTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        given(commentLikeRepository.findByMemberAndComment(member,comment)).willReturn(Optional.of(commentLike));

        commentLikeRepository.delete(commentLike);
        String result = likeService.commentLikeMinus(place.getId(),comment.getId(),member);

        assertThat(result).isEqualTo(LikeCancel);
    }

    @Test
    @DisplayName("댓글좋아요 카운트")
    public void commentLikeCountTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(commentRepository.findById(anyInt())).willReturn(Optional.of(comment));
        given(commentLikeRepository.countByComment(comment)).willReturn(Optional.of(0));

        List<String> result = likeService.likeCommentCount(comment.getId(),member);

        assertThat(result.get(0)).isEqualTo("0");
    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password(bCryptPasswordEncoder.encode("qwer4149!!"))
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .memberLat(0.00)
                .memberLng(0.00)
                .failedAttempt(0)
                .lockTime(new Date())
                .enabled(true)
                .accountNonLocked(true)
                .role(Role.ROLE_ADMIN)
                .build();
    }
    private Board getBoard(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(memberDto().getUserId())
                .boardTitle("test")
                .boardContents("test!!")
                .fileGroupId("free_sve345s")
                .readCount(0)
                .passWd("1234")
                .member(memberDto())
                .build();
    }
    private Comment comment(){
        return Comment
                .builder()
                .id(7)
                .replyContents("reply test")
                .replyWriter(member.getUserId())
                .replyPoint(3)
                .board(board)
                .member(member)
                .place(place)
                .build();
    }
    private Place getPlace(){
        return Place
                .builder()
                .id(1)
                .placeName("we")
                .placeStart("10:00")
                .placeClose("20:00")
                .placePhone("010-2345-5666")
                .placeAuthor(member.getUserId())
                .fileGroupId("place_sdc353")
                .reviewRate(0.0)
                .placeLat(123.34)
                .placeLng(23.35)
                .placeAddr1("sssss-ssss-ss")
                .placeAddr2("sdcsvefv")
                .build();
    }
    private CommentLike getCommentLike(){
        return CommentLike
                .builder()
                .member(member)
                .comment(comment)
                .build();
    }
    private Like getLike(){
        return Like
                .builder()
                .board(board)
                .member(member)
                .build();
    }
}
