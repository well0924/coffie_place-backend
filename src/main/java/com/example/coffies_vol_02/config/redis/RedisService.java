package com.example.coffies_vol_02.config.redis;

import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {
    private final BoardRepository boardRepository;

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;

    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    // 가게 key word 를 위한 setValues
    public void setValues(String key, String keyword) {
        ListOperations<Object, Object> listOperations = redisTemplate.opsForList();
        for (Object pastKeyword : Objects.requireNonNull(listOperations.range(key, 0, listOperations.size(key)))) {
            if (String.valueOf(pastKeyword).equals(keyword)) return;
        }
        if (listOperations.size(key) < 5) {
            listOperations.rightPush(key, keyword);
            //검색어 저장시 하루가 지나가면 자동으로 검색어를 삭제
            redisTemplate.expireAt(key, Date.from(ZonedDateTime.now().plusDays(1).toInstant()));
        } else if (listOperations.size(key) == 5) {
            listOperations.leftPop(key);
            listOperations.rightPush(key, keyword);
        }
    }

    //redis 검색어 목록
    public List<String> getSearchList(String key) {
        ListOperations listOperations = redisTemplate.opsForList();
        return listOperations.range(key, 0, listOperations.size(key));
    }

    //redis에서 값 제거
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    //redis에 저장된 값 가져오기.
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    //조회수 증가
    public void increasement(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.increment(key);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    //게시글 조회수
    public void boardViewCount(Integer boardId){
        String countKey = CacheKey.BOARD+"viewCount"+"::"+boardId;

        if(getData(countKey)==null){//조회가 처음이면
            setValues(countKey,String.valueOf(boardRepository.ReadCount(boardId)+1), Duration.ofMinutes(CacheKey.BOARD_EXPIRE_SEC));
        }else{//2번 이상이면 조회수를 증가.
            increasement(countKey);
        }
        log.info("viewCount!:",getData(countKey));
    }
    
    //게시글 조회수 디비에 반영
    @Scheduled(cron = "0/10 * * * * ?",zone = "Asia/Seoul")
    public void boardViewCountDB(){
        Set<String>viewKeys = keys("boardviewCount*");

        log.info("boardViewCount::"+viewKeys);

        if(Objects.requireNonNull(viewKeys).isEmpty())return;

        for(String viewKey : viewKeys){

            Integer boardId = Integer.parseInt(viewKey.split("::")[1]);
            Integer viewCount = Integer.parseInt(getData(viewKey));

            log.info("게시글 번호:"+boardId);
            log.info("조회수:"+viewCount);

            boardRepository.ReadCountUpToDB(boardId,viewCount);
            //캐시 삭제
            deleteValues(viewKey);
            deleteValues(CacheKey.BOARD+"viewCount"+"::"+boardId);
        }
    }


}
