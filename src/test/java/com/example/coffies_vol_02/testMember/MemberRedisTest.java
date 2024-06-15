package com.example.coffies_vol_02.testMember;

import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberRedisTest {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원 자동완성 검색")
    public void memberAutoCompleteTest(){

        HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();
        String key = "USERNAME_AUTOCOMPLETE::";
        hashOperations.put(key,"well4149",0);
        hashOperations.put(key,"well123",0);
        hashOperations.put(key,"well",0);

        //키값으로 저장된 값을 가져오기.=>총3개가 들어왔는지 보기.
        Set<String> a = hashOperations.keys(key);

        //redis scan을 사용해서 well로 시작하는 단어를 전부 다 검색하기.
        ScanOptions scanOptions = ScanOptions.scanOptions().match("well*").build();
        Cursor<Map.Entry<String,Integer>> cursor= hashOperations.scan(key, scanOptions);

        List<String> searchList = new ArrayList<>();

        while(cursor.hasNext()){
            Map.Entry<String,Integer> entry = cursor.next();
            searchList.add(entry.getKey());
        }

        System.out.println(searchList);
        assertThat(searchList.get(0)).isEqualTo("well4149");
    }

    @Test
    @DisplayName("가게 검색어 자동검색기능")
    public void redisMemberAutoCompleteTest(){
        //List<String>userIds = memberService.memberAutoSearch(MemberFactory.memberDto().getUserId());
        //assertThat(userIds.size()).isEqualTo(1);
    }
}
