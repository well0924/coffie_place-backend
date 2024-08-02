package com.example.coffies_vol_02.config.crawling;

import com.example.coffies_vol_02.config.queryDsl.TestQueryDslConfig;
import com.example.coffies_vol_02.config.util.CrawlingService;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceImageRepository;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CsvDBTest {

    @Autowired
    private CrawlingService crawlingService;

    private static final String TEST_CSV_FILE_PATH = "C:\\spring_work\\workspace\\CoffiesVol.02\\store_info_test.csv";

    private static final String CSV_FILE_PATH = "C:\\spring_work\\workspace\\CoffiesVol.02\\store_info.csv";

    @BeforeEach
    void setUp() throws IOException {

        File csvFile = new File(CSV_FILE_PATH);

        if (!csvFile.exists()) {
            throw new IOException("CSV 파일이 존재하지 않습니다: " + CSV_FILE_PATH);
        }
    }

    @Test
    @DisplayName("csv파일을 디비에 저장.")
    void testProcessCsvAndSaveToDatabase() {
        crawlingService.processCsvAndSaveToDatabase(CSV_FILE_PATH);
    }

}
