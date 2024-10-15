package com.example.coffies_vol_02.config.crawling;

import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;

@Log4j2
@Service
@AllArgsConstructor
public class CrawlingStoreService {

    private final CrawlingPlaceService crawlingPlaceService;

    /**
     * csv파일을 읽고 디비에 저장하기.
     * @param csvFilePath csv 파일 경로
     **/
    @Transactional
    public void processCsvAndSaveToDatabase(String csvFilePath) {

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {
            String[] values;

            csvReader.readNext(); // 헤더 스킵

            while ((values = csvReader.readNext()) != null) {
                //csv파일을 읽으면서 디비 저장
                crawlingPlaceService.saveOrUpdatePlaceAndImages(values);
            }
        } catch (IOException e) {
            log.error("Error occurred while reading CSV file: {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
