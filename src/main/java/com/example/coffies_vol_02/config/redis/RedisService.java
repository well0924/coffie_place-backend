package com.example.coffies_vol_02.config.redis;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<Object,Object> redisTemplate;
    private final RedisTemplate<String,String> hashRedisTemplate;

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
