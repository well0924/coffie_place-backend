package com.example.coffies_vol_02.config.crawling;

import com.example.coffies_vol_02.config.crawling.dto.PlaceCache;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CsvDBTest {

    @Autowired
    private CrawlingService crawlingService;

    @Autowired
    private CrawlingCacheService cacheService;

    private static final String TEST_CSV_FILE_PATH = "C:\\spring_work\\workspace\\CoffiesVol.02\\store_info_test.csv";

    private static final String CSV_FILE_PATH = "C:\\spring_work\\workspace\\CoffiesVol.02\\store_info.csv";

    @BeforeEach
    void setUp() throws IOException {

        File csvFile = new File(TEST_CSV_FILE_PATH);

        if (!csvFile.exists()) {
            throw new IOException("CSV 파일이 존재하지 않습니다: " + TEST_CSV_FILE_PATH);
        }
    }

    @Test
    @Disabled
    @DisplayName("csv 파일을 저장 하면서 Redis 캐싱")
    void testProcessCsvAndSaveToDatabaseCaching() {
        crawlingService.processCsvAndSaveToDatabase(TEST_CSV_FILE_PATH);
    }


    @Test
    @DisplayName("가게 정보 캐싱 & 조회")
    void testPlaceCaching() {
        // 가게 정보를 캐싱하는 테스트
        PlaceCache placeCache = PlaceCache.builder()
                .placeId("1")
                .placeName("Test Cafe")
                .placeAddr("123 Test St")
                .placeStart("09:00")
                .placeClose("22:00")
                .placePhone("123-456-7890")
                .placeAuthor("Test Author")
                .build();

        // 캐싱
        cacheService.cachePlace(placeCache);

        // 캐시 확인
        PlaceRequestDto cachedPlace = cacheService.getCachedPlace("1");

        // 검증
        assertThat(cachedPlace).isNotNull();
        assertThat(cachedPlace.getPlaceName()).isEqualTo("Test Cafe");
        assertThat(cachedPlace.getPlaceAddr()).isEqualTo("123 Test St");
        assertThat(cachedPlace.getPlaceStart()).isEqualTo("09:00");
        assertThat(cachedPlace.getPlaceClose()).isEqualTo("22:00");
        assertThat(cachedPlace.getPlacePhone()).isEqualTo("123-456-7890");
        assertThat(cachedPlace.getPlaceAuthor()).isEqualTo("Test Author");
    }

    @Test
    @DisplayName("가게 이미지 캐싱 목록 조회")
    void testFindPlaceImageListCaching() {
        String placeId = "3";

        // 메인 이미지 캐싱
        String mainImageUrl = "http://example.com/main-image.jpg";
       // cacheService.cacheImage(placeId, mainImageUrl, true);

        // 서브 이미지 캐싱
        String subImageUrl1 = "http://example.com/sub-image1.jpg";
        String subImageUrl2 = "http://example.com/sub-image2.jpg";
        //cacheService.cacheImage(placeId, subImageUrl1, false);
        //cacheService.cacheImage(placeId, subImageUrl2, false);

        // 메인 이미지 조회
        List<String> cachedMainImages = cacheService.getCachedImages(placeId, true);
        System.out.println(cachedMainImages.get(0));
        assertThat(cachedMainImages.get(0)).isEqualTo(mainImageUrl);

        // 서브 이미지 조회
        List<String> cachedSubImages = cacheService.getCachedImages(placeId, false);
        assertThat(cachedSubImages).containsExactly(subImageUrl1, subImageUrl2);
    }
}
