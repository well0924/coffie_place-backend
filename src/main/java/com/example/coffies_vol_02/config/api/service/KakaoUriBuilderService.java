package com.example.coffies_vol_02.config.api.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2
@Service
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    private static final String KAKAO_PLACE_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    public URI buildUriByAddressSearch(String address) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri(); // encode default utf-8
        log.info("[KakaoAddressSearchService buildUriByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }

    public URI buildUriByCategorySearch(String x, String y, int page){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_PLACE_SEARCH_ADDRESS_URL);
        log.info(uriBuilder);
        uriBuilder.queryParam("category_group_code", "CE7"); //음식점 FD6, 카페면 CE7
        uriBuilder.queryParam("x", x);
        uriBuilder.queryParam("y", y);
        uriBuilder.queryParam("radius", 3000); //3km 반경
        uriBuilder.queryParam("page", page); //페이지값 (하드코딩이 아닌 증가분으로 바꾸기)

        URI uri = uriBuilder.build().encode().toUri(); // encode default utf-8
        log.info(uri);
        return uri;
    }
}
