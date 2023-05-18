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

    public void increasement(String key){
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.increment(key);
    }
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

}
