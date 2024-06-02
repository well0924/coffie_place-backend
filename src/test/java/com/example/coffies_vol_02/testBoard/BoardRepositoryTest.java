package com.example.coffies_vol_02.testBoard;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.QBoard;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.config.QueryDsl.TestQueryDslConfig;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.like.domain.QLike;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired // 원래는 @PersistenceContext 을 많이 썼는데, 이제는 오토와이어도 잘됩니다.
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @BeforeEach // 기본적으로 테스트가 시작하기 전에 실행하는 함수
    void createTest() {
        queryFactory = new JPAQueryFactory(em);
    }
    @Test
    @DisplayName("게시물 목록")
    public void BoardListTest(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Page<BoardResponse>list = boardRepository.boardList(pageable);

        System.out.println(list.stream().toList());

        assertThat(list.get().toList()).isNotEmpty();
    }

    @Test
    @DisplayName("게시물 검색->작성자")
    public void BoardSearchTest(){
        String searchKeyword = "well4149";

        Pageable pageable = PageRequest.of(0,5,Sort.by("id").descending());

        Page<BoardResponse>list = boardRepository.findAllSearch(SearchType.w,searchKeyword,pageable);

        assertThat(list.toList()).isNotEmpty();
    }
    
    @Test
    @DisplayName("게시물 단일 조회")
    public void BoardDetailTest(){
        BoardResponse detail = boardRepository.boardDetail(1);

        assertThat(detail.id()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 이전글 번호 테스트")
    public void BoardPrevTest(){
        Board board = boardRepository.findById(10).get();
        //Optional<BoardNextPreviousInterface> previousBoard = boardRepository.findPreviousBoard(board.getCreatedTime());
        //System.out.println(previousBoard.get().getId());
    }

    @Test
    @DisplayName("회원이 좋아요 한 게시글(단일) 확인하기.->쿼리 테스트")
    public void likedMyArticle(){
        QBoard board = QBoard.board;
        QLike liked = QLike.like;

        queryFactory
                .selectFrom(board)
                .where(board.id.in(
                JPAExpressions
                        .select(liked.board.id)
                        .from(liked)
                        .where(liked.board.id.eq(15)
                                .and(liked.member.id.eq(1)))
        )).fetch().stream().forEach(result->{
                    log.info(result.getId());
                    log.info(result.getBoardTitle());
                    log.info(result.getBoardAuthor());
                    log.info(result.getBoardContents());
                    log.info(result.getFileGroupId());
                });
    }

    @Test
    @DisplayName("자유 게시글 이전글/다음글")
    public void test(){
        List<BoardNextPreviousInterface>result = boardRepository.findPreviousNextBoard(15);
        System.out.println(result.get(0).getBoardTitle());
    }
}
