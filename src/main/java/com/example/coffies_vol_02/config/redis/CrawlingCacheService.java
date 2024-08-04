package com.example.coffies_vol_02.config.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@AllArgsConstructor
public class CrawlingCacheService {

    private final RedisTemplate redisTemplate;

    /**
     * 가게 정보 캐싱저장
     * @param placeId 가게번호
     * @param placeInfo 캐싱을 할 가게객체
     **/
    public void cachePlace(String placeId, Map<String, String> placeInfo) {
        //캐싱 데이터 저장하기.
        redisTemplate.opsForHash().putAll(CacheKey.PLACE + ":" + placeId, placeInfo);
        //키 만료기간설정
        redisTemplate.expire(CacheKey.PLACE + ":" + placeId,Duration.ofSeconds(CacheKey.PLACE_EXPIRED_SEC));
    }

    /**
     * 가게 정보 캐싱가져오기.
     * @param placeId 가게번호
     **/
    public Map<Object, Object> getCachedPlace(String placeId) {
        return redisTemplate.opsForHash().entries(CacheKey.PLACE + ":" + placeId);
    }

    /**
     * 가게 정보 캐싱유무 확인
     * @param placeId 가게번호
     **/
    public boolean isPlaceCached(String placeId) {
        return redisTemplate.hasKey(CacheKey.PLACE + ":" + placeId);
    }

    /**
     * 가게 이미지 캐싱
     * @param placeId 가게이미지 번호
     * @param imageUrl 가게이미지 url
     **/
    public void cacheImage(String placeId, String imageUrl) {
        //키 형식은 PLACE_IMAGE : 이미지 번호(가게 번호)
        redisTemplate.opsForValue().set(CacheKey.PLACE_IMAGE + ":" + placeId, imageUrl);
        redisTemplate.expire(CacheKey.PLACE_IMAGE + ":" + placeId, Duration.ofSeconds(CacheKey.PLACE_IMAGE_EXPIRE_SEC));
    }

    /**
     * 가게 이미지 가져오기
     * @param placeId 가게이미지 번호
     **/
    public String getCachingPlaceImage(String placeId){
        return (String) redisTemplate.opsForValue().get(CacheKey.PLACE_IMAGE + ":" + placeId);
    }

    /**
     * 가게 이미지 캐싱 확인
     * @param placeId 가게이미지 번호
     **/
    public boolean isImageCached(String placeId) {
        //이미지 키 : 벨류 (PLACE_IMAGE:가게 번호)
        return redisTemplate.hasKey( CacheKey.PLACE_IMAGE + ":" + placeId);
    }
}
