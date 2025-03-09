package com.example.coffies_vol_02.config.api.service;

import com.example.coffies_vol_02.config.api.dto.DocumentDto;
import com.example.coffies_vol_02.config.api.dto.KakaoApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.KakaoPlaceApiResponseDto;
import com.example.coffies_vol_02.config.api.dto.PlaceDocumentDto;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoApiSearchService {

    private final RestTemplate restTemplate;

    private final RedisTemplate redisTemplate;

    private final KakaoUriBuilderService kakaoUriBuilderService;

    private final PlaceRepository placeRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    /**
     * 카카오 API 요청 (캐싱 적용)
     * - 동일한 주소에 대해 Redis 캐시 적용하여 API 요청 최소화
     * - API 실패 시 @Retryable로 재시도
     * - 재시도 후에도 실패하면 @Recover로 대체 데이터 제공
     */
    @Cacheable(value = "addressCache", key = "#address", unless = "#result == null")
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
     * 가게명 추출
     * @member 회원 객체
     * @return 추출된 가게이름 (리스트 타입)
     **/
    @Cacheable(value = "placeCache", key = "#member.memberLat + ',' + #member.memberLng", unless = "#result == null")
    @Retryable(
            value = {RuntimeException.class},//api가 호출이 되지 않은 경우에 runtimeException을 실행
            maxAttempts = 3,//재시도 횟수
            backoff = @Backoff(delay = 2000)//재시도 전에 딜레이 시간을 설정(ms)
    )
    public List<String>extractPlaceName(Member member){
        URI uri = kakaoUriBuilderService.buildUriByCategorySearch(
                String.valueOf(member.getMemberLng()),
                String.valueOf(member.getMemberLat()),
                1);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<KakaoPlaceApiResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoPlaceApiResponseDto.class);
        KakaoPlaceApiResponseDto result = response.getBody();

        if (result == null || result.getDocumentList() == null) {
            log.warn("Failed to fetch place names. Returning fallback list.");
            return List.of("Fallback Place 1", "Fallback Place 2");
        }

        return result.getDocumentList().stream()
                .map(PlaceDocumentDto::getPlaceName)
                .collect(Collectors.toList());
    }

    @Recover
    public KakaoApiResponseDto recover(CustomExceptionHandler e, String address) {
        log.error("All retries failed. Using fallback response. Address: {}, Error: {}", address, e.getErrorCode().getMessage());

        KakaoApiResponseDto cachedResponse = (KakaoApiResponseDto) redisTemplate.opsForValue().get("addressCache::" + address);
        if (cachedResponse != null) {
            log.info(" Redis 캐싱된 데이터 반환: {}", address);
            return cachedResponse;
        }

        // DB에서 기존 가게 데이터를 조회
        List<Place> places = placeRepository.findPlacesByName(Collections.singletonList(address));
        if (!places.isEmpty()) {
            log.info(" DB에서 조회한 데이터 반환: {}", address);

            // DB 데이터 → KakaoApiResponseDto로 변환
            List<DocumentDto> documents = places.stream()
                    .map(DocumentDto::new) // Place → DocumentDto 변환
                    .collect(Collectors.toList());

            return new KakaoApiResponseDto(documents);
        }
        //모드 방법이 실패한 경우 빈 리스트를 출력.
        return getFallbackResponse();
    }

    private KakaoApiResponseDto getFallbackResponse() {
        KakaoApiResponseDto fallbackResponse = new KakaoApiResponseDto();
        fallbackResponse.setDocumentList(Collections.emptyList());
        return fallbackResponse;
    }
}
