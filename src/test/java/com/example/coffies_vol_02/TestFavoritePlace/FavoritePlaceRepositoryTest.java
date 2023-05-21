package com.example.coffies_vol_02.TestFavoritePlace;

import com.example.coffies_vol_02.config.TestQueryDslConfig;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.favoritePlace.repository.FavoritePlaceRepository;
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
public class FavoritePlaceRepositoryTest {
    @Autowired
    private FavoritePlaceRepository favoritePlaceRepository;

    @Test
    @DisplayName("위시리스트 목록")
    public void wishListTest(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Page<FavoritePlaceDto>list = favoritePlaceRepository.favoritePlaceWishList(pageable,"well4149");

        System.out.println(list.toList());

        assertThat(list).isNotEmpty();
    }
}
