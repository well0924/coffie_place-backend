package com.example.coffies_vol_02.TestNotice;

import com.example.coffies_vol_02.Config.TestQueryDslConfig;
import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
import com.example.coffies_vol_02.Notice.domain.QNoticeBoard;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.repository.NoticeBoardRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class NoticeBoardRepositoryTest {
    @Autowired
    private NoticeBoardRepository noticeBoardRepository;

    @Test
    @DisplayName("공지게시판 목록")
    public void noticeBoardList()throws Exception{
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Page<NoticeBoard>list1  = noticeBoardRepository.findAll(pageable);
        System.out.println(list1.stream().toList());
        assertThat(list1).isNotEmpty();

        Page<NoticeBoardDto.BoardResponseDto>list2 = noticeBoardRepository.findAllList(pageable);
        System.out.println(list2.get().toList());
        assertThat(list2).isNotEmpty();
    }

    @Test
    @DisplayName("공지게시판 검색")
    public void noticeBoardSearch()throws Exception{
        String keyword = "well4149";

        QNoticeBoard qNoticeBoard = QNoticeBoard.noticeBoard;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression booleanExpression = qNoticeBoard.noticeWriter.contains(keyword);
        builder.and(booleanExpression);

        Page<NoticeBoard>result1 = noticeBoardRepository.findAll(builder,pageable);
        Page<NoticeBoardDto.BoardResponseDto>result2 = noticeBoardRepository.findAllSearchList(keyword,pageable);
        System.out.println(result1.get().toList());
        System.out.println(result2.get().toList());
        assertThat(result1);
        assertThat(result2);
    }
}
