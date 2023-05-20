package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.config.TestQueryDslConfig;
import com.example.coffies_vol_02.place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaceRepositoryTest {
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceImageRepository placeImageRepository;

    @Test
    @DisplayName("가게 평점 top5 조회")
    public void Top5ListTest(){
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<PlaceDto.PlaceResponseDto>top5List = placeRepository.placeTop5(pageable);
        
        System.out.println(top5List);
        
        assertThat(top5List).isNotEmpty();
    }

    @Test
    @DisplayName("가게 목록-무한 스크롤")
    public void placeListTest(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Slice<PlaceDto.PlaceResponseDto>placeList = placeRepository.placeList(pageable,null);

        System.out.println(placeList);
        assertThat(placeList);
    }

    @Test
    @DisplayName("")
    public void PlaceListSearchTest(){
        
    }
}
