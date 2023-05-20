package com.example.coffies_vol_02.TestBoard;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.config.TestQueryDslConfig;
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

        Page<Board>result1 = boardRepository.findAll(pageable);
        Page<BoardResponseDto>list = boardRepository.boardList(pageable);

        System.out.println(list.stream().toList());
        System.out.println(result1.stream().toList());

        assertThat(list.get().toList()).isNotEmpty();
        assertThat(result1.get().toList()).isNotEmpty();
    }

    @Test
    @DisplayName("게시물 검색->작성자")
    public void BoardSearchTest(){
        String searchKeyword = "well4149";

        Pageable pageable = PageRequest.of(0,5,Sort.by("id").descending());
        
        Page<BoardResponseDto>list = boardRepository.findAllSearch(searchKeyword,pageable);
        assertThat(list.toList()).isNotEmpty();
    }
}
