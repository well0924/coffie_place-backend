package com.example.coffies_vol_02.config.redis;

import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {

    private final PlaceRepository placeRepository;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String,Object> redisTemplates;
    private final RedisTemplate<String,String>redisTemplate;
    private final MemberRepository memberRepository;

    //Redis 값 저장
    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    /**
     * 가게 key word 를 위한 setValues
     * @param key Redis에 저장된 키값
     * @param keyword 가게명
     * @param memberId 회원번호
     **/
    public void createPlaceNameLog(String key, String keyword,Integer memberId) {
        Optional<Member>member = memberRepository.findById(memberId);

    }
    
    //가게 검색어 목록을 보여주는 기능
    public List<String>ListPlaceNameLog(){
        return null;
    }

    //가게 검색어 개별 삭제
    public void deletePlaceNameLog(){

    }

    /**
     *  회원 이름 자동완성기능
     * @author 양경빈
     * @param userId 회원 아이디
     * @return searchList 회원 검색에 필요한 목록들
     **/
    public List<String> memberAutoSearch(String userId){

        HashOperations<String,String,Object>hashOperations = redisTemplates.opsForHash();

        List<Member>nameList = memberRepository.findAll();

        Map<String,Object> nameDateMap = nameList.stream().collect(Collectors.toMap(Member::getUserId,Member::getId));
        //redisHash 에 저장
        hashOperations.putAll(CacheKey.USERNAME,nameDateMap);

        ScanOptions scanOptions = ScanOptions.scanOptions().match(userId+"*").build();

        Cursor<Map.Entry<String,Object>> cursor= hashOperations.scan(CacheKey.USERNAME, scanOptions);

        List<String> searchList = new ArrayList<>();

        while(cursor.hasNext()){
            Map.Entry<String,Object> entry = cursor.next();
            searchList.add(entry.getKey());
        }

        return searchList;
    }

    /**
     * 가게 댓글 평점 저장
     * @param rating 가게 댓글 평점
     * @param storeId 가게 번호
     **/
    public void saveRating(String storeId, double rating) {
        redisTemplates.opsForZSet().add("storeRatings", storeId, rating);
        System.out.println("Saved rating for store " + storeId + " with rating " + rating);
    }

    /**
     * 평점이 높은 가게 TOP5
     * 가게 댓글 평점을 계산해서 평점이 높은 가게 5개를 나타내는 기능
     **/
    public List<Place> getTopRatedStores() {

        Set<String> topStores = redisTemplate.opsForZSet().reverseRange("storeRatings", 0, 4);

        List<Place>storeList = new ArrayList<>();

        for (String storeId : topStores) {
            placeRepository.findById(Integer.parseInt(storeId))
                    .ifPresent(store -> storeList.add(store));
        }

        return storeList;
    }
}
