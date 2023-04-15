package com.example.coffies_vol_02.TestLike;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Like.repository.LikeRepository;
import com.example.coffies_vol_02.Like.service.LikeService;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
public class TestLikeService {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @InjectMocks
    private LikeService likeService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    Member member;
    Board board;
    Like like;
    private static final String LikeSuccess ="좋아요 추가";
    private static final String LikeCancel ="좋아요 취소";
    @Mock
    private LikeRepository likeRepository;

    @BeforeEach
    public void init(){
        member = memberDto();
        board = getBoard();
        like = getLike();
    }

    @Test
    @DisplayName("좋아요 추가")
    public void boardLikeCreateTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(getLike()));

        String result = likeService.createBoardLike(board.getId(),member);

        assertThat(result).isEqualTo(LikeSuccess);
    }
    @Test
    @DisplayName("좋아요 삭제")
    public void boardLikeCancelTest(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));
        given(likeRepository.findByMemberAndBoard(member,board)).willReturn(Optional.of(getLike()));
        String result = likeService.cancelLike(board.getId(),member);
        assertThat(result).isEqualTo(LikeCancel);
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
    private Like getLike(){
        return Like
                .builder()
                .board(board)
                .member(member)
                .build();
    }
}
