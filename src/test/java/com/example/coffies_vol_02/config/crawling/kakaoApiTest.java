package com.example.coffies_vol_02.config.crawling;

import com.example.coffies_vol_02.config.api.dto.KakaoPlaceApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.PlaceDocumentDto;
import com.example.coffies_vol_02.config.api.service.KakaoUriBuilderService;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@SpringBootTest
public class kakaoApiTest {

    @Autowired
    private KakaoUriBuilderService kakaoUriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlaceRepository placeRepository;

    @BeforeEach
    public void init(){
        this.kakaoUriBuilderService = new KakaoUriBuilderService();
    }


    @Test
    @DisplayName(value="회원 위경도를 기준으로 해서 가게 데이터를 가져오기.")
    public void SearchTest(){
        Set<URI>uris = new HashSet<>();

        int page =1;
        //회원 위경도 추가하기.
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

            for (PlaceDocumentDto document : result.getDocumentList()) {
                log.info("placeName::::" + document.getPlaceName());
            }

            log.info("placeNames::::"+result.getDocumentList());
            log.info("apiResult:::"+result.getMetaDto().getPageableCount());
            log.info("apiResult(검색된 총 갯수):::"+result.getMetaDto().getTotalCount());
            log.info("apiResult:::"+result.getMetaDto().getIsEnd());
            int maxPage = result.getMetaDto().getPageableCount()/15;

            log.info("totalPage::"+maxPage);
        }

        List<String> placeNames = new ArrayList<>();

        for (KakaoPlaceApiResponseDto result : results) {
            for (PlaceDocumentDto document : result.getDocumentList()) {
                placeNames.add(document.getPlaceName());
                log.info("placeName::::" + document.getPlaceName());
            }
        }

        List<Place> list = placeRepository.findPlacesByName(placeNames);

        System.out.println(list.stream().map(p->new PlaceResponseDto(p)).collect(Collectors.toList()));
    }
}
