package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.board.domain.dto.response.BoardNextInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.config.QueryDsl.TestQueryDslConfig;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardRepositoryTest {
    @Autowired
    private BoardRepository boardRepository;

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
        
        Page<BoardResponse>list = boardRepository.findAllSearch(searchKeyword,pageable);
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
        Optional<BoardNextPreviousInterface> result = boardRepository.findPreviousBoard(6);

        System.out.println("이전글:"+result);
        System.out.println("이전글 번호:"+result.get().getId());
        System.out.println("이전글 제목:"+result.get().getBoardTitle());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
    }
    @Test
    @DisplayName("게시글 이전글 번호 테스트-실패")
    public void BoardPrevTestFail(){
        Optional<BoardNextPreviousInterface> result = boardRepository.findPreviousBoard(1);

        if(result.isEmpty()){
            System.out.println("글이없습니다.");
        }
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("게시글 다음글 번호 테스트")
    public void BoardNextTest(){
        Optional<BoardNextInterface> result = boardRepository.findNextBoard(6);

        System.out.println(result);
        System.out.println(result.get().getId());
        System.out.println(result.get().getBoardTitle());
    }

    @Test
    @DisplayName("게시글 다음글 번호 테스트 실패")
    public void BoardNextTestFail(){
        Optional<BoardNextInterface> result = boardRepository.findNextBoard(2000);

        if(result.isEmpty()){
            System.out.println("다음글이 없습니다.");
        }

        assertThat(result).isEmpty();
    }

}
