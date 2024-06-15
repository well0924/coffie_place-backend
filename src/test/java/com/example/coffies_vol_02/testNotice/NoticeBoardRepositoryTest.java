package com.example.coffies_vol_02.testNotice;

import com.example.coffies_vol_02.config.QueryDsl.TestQueryDslConfig;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
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
    public void noticeBoardList(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Page<NoticeBoard>list1  = noticeBoardRepository.findAll(pageable);

        assertThat(list1).isNotEmpty();

        Page<NoticeResponse>list2 = noticeBoardRepository.findAllList(pageable);

        assertThat(list2).isNotEmpty();
    }

    @Test
    @DisplayName("공지게시판 검색")
    public void noticeBoardSearch(){
        String keyword = "well4149";

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        Page<NoticeResponse>result2 = noticeBoardRepository.findAllSearchList(SearchType.w,keyword,pageable);

        assertThat(result2.toList()).isNotEmpty();
        assertThat(result2.get().toList().get(0).noticeWriter()).isEqualTo(keyword);
    }
}
