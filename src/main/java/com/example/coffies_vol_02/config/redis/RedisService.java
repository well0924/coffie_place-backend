package com.example.coffies_vol_02.config.redis;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.*;
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

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;

    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    // key word 를 위한 setValues
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

    public void increasement(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.increment(key);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

}
