package com.example.coffies_vol_02.config.api.service;

import com.example.coffies_vol_02.config.api.dto.KakaoApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.KakaoPlaceApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.PlaceDocumentDto;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoApiSearchService {

    private final RestTemplate restTemplate;

    private final KakaoUriBuilderService kakaoUriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    /**
     * kakao map api 검색
     * @param address 가게검색주소
     * @return KakaoApiResponseDto
     **/
    @Retryable(
            value = {RuntimeException.class},//api가 호출이 되지 않은 경우에 runtimeException을 실행
            maxAttempts = 3,//재시도 횟수
            backoff = @Backoff(delay = 2000)//재시도 전에 딜레이 시간을 설정(ms)
    )
    public KakaoApiResponseDto requestAddressSearch(String address) {

        if(ObjectUtils.isEmpty(address)) return null;

        URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class).getBody();
    }

    /**
     *
     **/
    @Retryable(
            value = {RuntimeException.class},//api가 호출이 되지 않은 경우에 runtimeException을 실행
            maxAttempts = 3,//재시도 횟수
            backoff = @Backoff(delay = 2000)//재시도 전에 딜레이 시간을 설정(ms)
    )
    public List<String>extractPlaceName(Member member){
        Set<URI> uris = new HashSet<>();

        int page =1;
        uris.add(kakaoUriBuilderService.buildUriByCategorySearch(
                String.valueOf(member.getMemberLng()),
                String.valueOf(member.getMemberLat()),
                page));

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
        }

        List<String> placeNames = new ArrayList<>();

        for (KakaoPlaceApiResponseDto result : results) {
            for (PlaceDocumentDto document : result.getDocumentList()) {
                placeNames.add(document.getPlaceName());
                log.info("placeName::::" + document.getPlaceName());
            }
        }

        return placeNames;
    }

    @Recover
    public KakaoApiResponseDto recover(String address, CustomExceptionHandler customExceptionHandler){
        log.error("All the retries failed. address: {}, error : {}", address, customExceptionHandler.getErrorCode().getMessage());
        return null;
    }
}
