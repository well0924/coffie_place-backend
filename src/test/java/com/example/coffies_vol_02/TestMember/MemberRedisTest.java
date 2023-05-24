package com.example.coffies_vol_02.TestMember;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MemberRedisTest {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Test
    @DisplayName("회원 자동완성 검색")
    public void memberAutoCompleteTest(){
        String key = "USERNAME::";

        HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();

        hashOperations.put(key,"well4149",0);
        hashOperations.put(key,"well123",0);
        hashOperations.put(key,"well",0);

        //키값으로 저장된 값을 가져오기.=>총3개가 들어왔는지 보기.
        Set<String> a = hashOperations.keys(key);
        assertThat(a).size().isEqualTo(3);

        //redis scan을 사용해서 well로 시작하는 단어를 전부 다 검색하기.
        ScanOptions scanOptions = ScanOptions.scanOptions().match("well*").build();
        Cursor<Map.Entry<String,Integer>> cursor= hashOperations.scan(key, scanOptions);

        List<String> searchList = new ArrayList<>();

        while(cursor.hasNext()){
            Map.Entry<String,Integer> entry = cursor.next();
            searchList.add(entry.getKey());
        }

        assertThat(searchList.get(0)).isEqualTo("well4149");
    }
}
