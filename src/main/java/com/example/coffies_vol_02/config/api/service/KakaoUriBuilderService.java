package com.example.coffies_vol_02.config.api.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2
@Service
public class KakaoUriBuilderService {
    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public URI buildUriByAddressSearch(String address) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri(); // encode default utf-8
        log.info("[KakaoAddressSearchService buildUriByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }

}
