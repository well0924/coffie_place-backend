package com.example.coffies_vol_02.Config.Redis;

import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Board.service.BoardService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String,String> redisTemplate;
    private final BoardRepository boardRepository;

    public void setValues(String key,String value,Duration duration){
        ValueOperations<String,String>values = stringRedisTemplate.opsForValue();
        values.set(key,value,duration);
    }

    //redis에서 값 제거
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    //redis에 저장된 값 가져오기.
    public String getData(String key){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    //게시글 조회수
    public void boardViewCount(Integer boardId){
        String countKey = CacheKey.BOARD+"viewCount"+"::"+boardId;

        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        System.out.println("result:"+valueOperations);
        String getResult = getData(countKey);
        System.out.println("???:"+getResult);

        if(getData(countKey)==null){//조회가 처음이면
            setValues(countKey,String.valueOf(boardRepository.ReadCount(boardId)+1), Duration.ofMinutes(CacheKey.BOARD_EXPIRE_SEC));
        }else if(getData(countKey)!=null){
            //2번 이상이면 조회수를 증가.
            valueOperations.increment(countKey);
        }
    }

    //게시글 조회수 반영(추후에 개선 필요...) 10초마다 실행.
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

            deleteValues(viewKey);
            deleteValues(CacheKey.BOARD+"viewCount"+"::"+boardId);
        }
    }
    
    //게시글 좋아요 기능
    
    //가게 좋아요 기능
    
    //가게 top5기능
}
