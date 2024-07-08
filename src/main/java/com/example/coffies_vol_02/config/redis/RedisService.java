package com.example.coffies_vol_02.config.redis;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRecentSearchDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {

    private final PlaceRepository placeRepository;

    private final RedisTemplate<String,PlaceRecentSearchDto> redisTemplates;

    private final RedisTemplate<String,String>redisTemplate;

    private final MemberRepository memberRepository;

    /**
     * 최근 검색한 가게명과 날짜를 저장
     * @param name 최근검색어
     * @param memberId 회원번호
     **/
    public void createPlaceNameLog(Integer memberId, String name) {
        Optional<Member>member = memberRepository.findById(memberId);

        String now = LocalDateTime.now().toString();
        //key값
        String key = CacheKey.PLACE + member.orElseThrow().getId();
        //value 값
        PlaceRecentSearchDto value = PlaceRecentSearchDto
                .builder()
                .name(name)
                .createdTime(now)
                .build();

        //검색어 사이즈
        Long size =redisTemplates.opsForList().size(key);

        log.info(size);

        if(size != null && size == 5){
            //rightPop을 사용해서 가장 오래된 데이터를 삭제
            redisTemplates.opsForList().rightPop(key);
        }
        //데이터 저장
        redisTemplates.opsForList().leftPush(key,value);
    }
    
    /**
     * 가게 검색어 목록을 보여주는 기능
     * @param memberId 회원 번호
     **/
    public List<PlaceRecentSearchDto>ListPlaceNameLog(Integer memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        String key = CacheKey.PLACE + member.getId();

        List<PlaceRecentSearchDto> logs = redisTemplates.opsForList().
                range(key, 0, 5);

        log.info(logs);

        if(logs.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.SEARCH_LOG_NOT_EXIST);
        }

        return logs;
    }

    /**
     * 가게 검색어 전체삭제
     * @param memberId 회원 번호
     **/
    public void deletePlaceNameLog(Integer memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        String key = CacheKey.PLACE + member.getId();

        log.info("keys:::"+ key);
        Boolean isDeleted = redisTemplate.delete(key);

        if (!isDeleted) {
            throw new CustomExceptionHandler(ERRORCODE.SEARCH_LOG_NOT_EXIST);
        }
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
        log.info(nameList.stream().toList());
        Map<String,Object> nameDateMap = nameList.stream()
                .collect(Collectors.toMap(Member::getUserId,Member::getId));
        log.info(nameDateMap);
        //redisHash 에 저장
        hashOperations.putAll(CacheKey.USERNAME,nameDateMap);

        // redisHash에 저장된 데이터 확인
        Map<String, Object> redisData = hashOperations.entries(CacheKey.USERNAME);
        log.info("Redis Data: " + redisData);

        //검색조건 설정
        String matchPattern = "\"" + userId + "*";
        ScanOptions scanOptions = ScanOptions.scanOptions().match(matchPattern).count(10000).build();
        Cursor<Map.Entry<String,Object>> cursor= hashOperations.scan(CacheKey.USERNAME, scanOptions);

        List<String> searchList = new ArrayList<>();

        while(cursor.hasNext()){
            Map.Entry<String,Object> entry = cursor.next();
            log.info(entry);
            searchList.add(entry.getKey());
        }
        log.info(searchList);
        return searchList;
    }

    /**
     * 가게 댓글 평점 저장
     * @param rating 가게 댓글 평점
     * @param storeId 가게 번호
     **/
    public void saveRating(String storeId, double rating) {
        redisTemplate.opsForZSet().add("storeRatings::"+storeId, storeId, rating);
        System.out.println("평점 저장::: " + storeId + " with rating " + rating);
    }

    /**
     * 가게 댓글 평점 삭제
     * @param storeId 가게 번호
     **/
    public void deleteRating(String storeId) {
        Long removedCount = redisTemplate.opsForZSet().remove("storeRatings::" + storeId, storeId);
        if (removedCount != null && removedCount > 0) {
            System.out.println("평점 삭제::: " + storeId);
        } else {
            System.out.println("삭제할 평점이 없습니다: " + storeId);
        }
    }

    /**
     * 평점이 높은 가게 TOP5
     * 가게 댓글 평점을 계산해서 평점이 높은 가게 5개를 나타내는 기능
     * 추가로 동점자를 고려해서 구현
     **/
    public List<PlaceResponseDto> getTopRatedStores() {

        Set<String> topStores = redisTemplate.opsForZSet().reverseRange("storeRatings", 0, 4);

        List<Place>storeList = new ArrayList<>();

        //redis에 평점이 저장이 안되어있거나 없는 경우
        if(topStores.isEmpty() || topStores == null){
            return placeRepository.placeTop5();
        }

        for (String storeId : topStores) {
            placeRepository.findById(Integer.parseInt(storeId))
                    .ifPresent(storeList::add);
        }

        // 동점 처리: 리뷰 평점이 같을 경우 가게 번호를 기준으로 정렬
        storeList.sort((a, b) -> {
            int rateComparison = b.getReviewRate().compareTo(a.getReviewRate());
            if (rateComparison != 0) {
                return rateComparison;
            }
            return a.getId().compareTo(b.getId());
        });

        return storeList.stream().map(PlaceResponseDto::new).collect(Collectors.toList());
    }
}
