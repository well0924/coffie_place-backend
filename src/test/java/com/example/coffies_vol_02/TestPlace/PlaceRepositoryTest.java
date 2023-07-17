package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.config.QueryDsl.TestQueryDslConfig;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaceRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceImageRepository placeImageRepository;

    @Test
    @DisplayName("가게 평점 top5 조회")
    public void Top5ListTest(){
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<PlaceResponseDto>top5List = placeRepository.placeTop5(pageable);

        log.info("top5List: "+top5List);

        assertThat(top5List).isNotEmpty();
    }

    @Test
    @DisplayName("가게 목록-무한 스크롤")
    public void placeListTest(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Slice<PlaceResponseDto>placeList = placeRepository.placeList(pageable,null);

        log.info("가게목록:"+placeList);

        assertThat(placeList).isNotEmpty();
    }

    @Test
    @DisplayName("가게 검색")
    public void PlaceListSearchTest(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("id"));

        Page<PlaceResponseDto>searchList = placeRepository.placeListSearch(SearchType.p,"릴렉스",pageable);

        log.info("검색결과:"+searchList.stream().toList());

        assertThat(searchList).isNotEmpty();
        assertThat(searchList.toList().get(0).getPlaceName()).isEqualTo("릴렉스");
    }
}
