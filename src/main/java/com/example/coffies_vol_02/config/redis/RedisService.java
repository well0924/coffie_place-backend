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
        redisTemplate.opsForZSet().add("storeRatings", storeId, rating);
        System.out.println("평점 저장::: " + storeId + " with rating " + rating);
    }

    /**
     * 평점이 높은 가게 TOP5
     * 가게 댓글 평점을 계산해서 평점이 높은 가게 5개를 나타내는 기능
     **/
    public List<PlaceResponseDto> getTopRatedStores() {

        Set<String> topStores = redisTemplate.opsForZSet().reverseRange("storeRatings", 0, 4);

        List<Place>storeList = new ArrayList<>();

        for (String storeId : topStores) {
            placeRepository.findById(Integer.parseInt(storeId))
                    .ifPresent(storeList::add);
        }

        return storeList.stream().map(PlaceResponseDto::new).collect(Collectors.toList());
    }
}
