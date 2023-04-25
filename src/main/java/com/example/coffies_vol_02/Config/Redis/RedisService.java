package com.example.coffies_vol_02.Config.Redis;

import com.example.coffies_vol_02.Board.service.BoardService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String,String> redisTemplate;
    private final BoardService boardService;

    public void setValues(String key, String data) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    //redis에 값 꺼내기
    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
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

    //게시글 조회수
    public void boardViewCount(Integer boardId){
        String countKey = CacheKey.BOARD+"viewCount"+"::"+boardId;
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();

        if(getValues(countKey)==null){//조회가 처음이면
            setValues(countKey,String.valueOf(boardService.updateView(boardId)));
        }else{//2번 이상이면 조회수를 증가.
            valueOperations.increment(countKey);
        }
        log.info("viewCount!:",getValues(countKey));
    }
}
