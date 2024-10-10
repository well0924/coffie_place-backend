package com.example.coffies_vol_02.config.crawling;

import com.example.coffies_vol_02.config.crawling.dto.PlaceCache;
import com.example.coffies_vol_02.config.crawling.dto.PlaceImageCache;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@AllArgsConstructor
public class CrawlingCacheService {

    private final RedisTemplate<String,String> redisTemplate;

    // 가게 정보를 Redis에 캐싱
    public void cachePlace(PlaceCache placeCache) {
        String placeKey = "PLACE:" + placeCache.getPlaceId();
        redisTemplate.opsForHash().put(placeKey, "placeName", placeCache.getPlaceName());
        redisTemplate.opsForHash().put(placeKey, "placeAddr", placeCache.getPlaceAddr());
        redisTemplate.opsForHash().put(placeKey, "placeStart", placeCache.getPlaceStart());
        redisTemplate.opsForHash().put(placeKey, "placeClose", placeCache.getPlaceClose());
        redisTemplate.opsForHash().put(placeKey, "placePhone", placeCache.getPlacePhone());
        redisTemplate.opsForHash().put(placeKey, "placeAuthor", placeCache.getPlaceAuthor());
    }

    // 가게 이미지 URL을 Redis에 캐싱
    public void cacheImage(PlaceImage placeImage) {
        String cacheKey = placeImage.getIsTitle().equals("Y")
                ? "PLACE_IMAGE:MAIN:" + placeImage.getPlace().getId()
                : "PLACE_IMAGE:SUB:" + placeImage.getPlace().getId();
        log.info("caching!");
        // PlaceImageCache 객체 생성
        PlaceImageCache imageCache = PlaceImageCache
                .builder()
                .imageId(placeImage.getId().toString())
                .placeId(placeImage.getPlace().getId().toString()) // Place ID
                .imageUrl(placeImage.getThumbFileImagePath()) // 이미지 URL
                .fileGroupId(placeImage.getFileGroupId())
                .fileType(placeImage.getFileType())
                .imgGroup(placeImage.getImgGroup())
                .thumbFilePath(placeImage.getThumbFilePath())
                .thumbFileImagePath(placeImage.getThumbFileImagePath())
                .storedName(placeImage.getStoredName())
                .originName(placeImage.getOriginName())
                .imgUploader(placeImage.getImgUploader())
                .isTitle(placeImage.getIsTitle())
                .build();

        // 캐시 저장 (직렬화하여 문자열로 저장)
        try {
            String imageCacheJson = new ObjectMapper().writeValueAsString(imageCache);
            redisTemplate.opsForList().rightPush(cacheKey, imageCacheJson); // 리스트에 추가
            log.info("Cached image: {}", imageCache);
        } catch (JsonProcessingException e) {
            log.error("Error serializing imageCache to JSON: {}", e.getMessage());
        }
    }

    // 가게 정보 조회
    public PlaceRequestDto getCachedPlace(String placeId) {
        Map<Object, Object> cachedPlace = redisTemplate.opsForHash().entries("PLACE:" + placeId);
        if (cachedPlace == null || cachedPlace.isEmpty()) {
            log.warn("No cached place found for placeId: {}", placeId);
            return null; // null 반환 또는 적절한 처리
        }
        return PlaceRequestDto.createPlaceRequestDtoFromCache(cachedPlace);
    }

    // 가게 이미지 URL 조회 (Main, Sub 이미지)
    public List<PlaceImageCache> getCachedImages(String placeId, boolean isMainImage) {
        String cacheKey = isMainImage ? "PLACE_IMAGE:MAIN:" + placeId : "PLACE_IMAGE:SUB:" + placeId;

        // 모든 이미지 URL을 가져오기
        List<String> imageJsonList = redisTemplate.opsForList().range(cacheKey, 0, -1);
        List<PlaceImageCache> images = new ArrayList<>();

        if (imageJsonList != null && !imageJsonList.isEmpty()) {
            // JSON 문자열을 PlaceImageCache 객체로 역직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            for (String imageJson : imageJsonList) {
                try {
                    PlaceImageCache imageCache = objectMapper.readValue(imageJson, PlaceImageCache.class);
                    images.add(imageCache);
                } catch (JsonProcessingException e) {
                    log.error("Error deserializing imageCache from JSON: {}", e.getMessage());
                }
            }
        } else {
            log.warn("No images found for placeId: {}", placeId);
        }

        return images; // PlaceImageCache 객체 리스트 반환
    }
}
