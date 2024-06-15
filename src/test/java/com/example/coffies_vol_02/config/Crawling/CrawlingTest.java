package com.example.coffies_vol_02.config.Crawling;

import com.example.coffies_vol_02.config.api.dto.KakaoPlaceApiResponseDto;
import com.example.coffies_vol_02.config.api.service.KakaoUriBuilderService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@SpringBootTest
public class CrawlingTest {

    @Autowired
    private KakaoUriBuilderService kakaoUriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    public void init(){
        this.kakaoUriBuilderService = new KakaoUriBuilderService();
    }


    @Test
    @DisplayName(value="위경도를 기준으로 해서 데이터를 가져오기.")
    public void SearchTest(){
        Set<URI>uris = new HashSet<>();

        int page =1;

        uris.add(kakaoUriBuilderService.buildUriByCategorySearch(
                String.valueOf(127.01963902071),
                String.valueOf(37.6426557894829),
                page));
        log.info("result2:::::"+uris);

        HttpHeaders headers = new HttpHeaders();

        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        List<KakaoPlaceApiResponseDto> results = new ArrayList<>();

        for(URI uri : uris){
            KakaoPlaceApiResponseDto result = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                    KakaoPlaceApiResponseDto.class).getBody();
            results.add(result);

            log.info("apiResult:::"+result.getDocumentList());
            log.info("apiResult:::"+result.getMetaDto().getPageableCount());
            log.info("apiResult(검색된 총 갯수):::"+result.getMetaDto().getTotalCount());
            log.info("apiResult:::"+result.getMetaDto().getIsEnd());
            log.info("apiResult:::"+result.getDocumentList());
            int maxPage = result.getMetaDto().getPageableCount()/15;

            log.info("totalPage::"+maxPage);
        }
        //results.add(restTemplate.exchange(uri,HttpMethod.GET,httpEntity,KakaoPlaceApiResponseDto.class).getBody());

        log.info(results);
    }
}
