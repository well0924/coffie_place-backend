package com.example.coffies_vol_02.testMember;

import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberRedisTest {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisService redisService;

    @BeforeEach
    public void setup(){
        // Redis 클리어
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 데이터베이스에 더미 회원 추가
//        List<Member> members = IntStream.range(0, TEST_MEMBER_COUNT)
//                .mapToObj(i -> Member.builder()
//                        .id(i)
//                        .userId("user" + i)
//                        .password("password" + i)
//                        .memberName("name" + i)
//                        .userPhone("phone" + i)
//                        .userGender("gender" + i)
//                        .userAge("age" + i)
//                        .userEmail("email" + i)
//                        .userAddr1("addr1" + i)
//                        .userAddr2("addr2" + i)
//                        .enabled(true)
//                        .accountNonLocked(true)
//                        .failedAttempt(0)
//                        .lockTime(null)
//                        .memberLng(i * 0.1)
//                        .memberLat(i * 0.1)
//                        .role(Role.ROLE_USER)
//                        .memberStatus(MemberStatus.NON_USER_LOCK)
//                        .build())
//                .collect(Collectors.toList());
//        memberRepository.saveAll(members);
        //List<Member>members = memberRepository.findAll();
        // Redis에 회원 데이터 저장
//        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
//        Map<String, String> memberMap = members.stream()
//                .collect(Collectors.toMap(Member::getUserId, member -> member.getId().toString()));
//
//        hashOperations.putAll("usernames", memberMap);
    }

    @AfterEach
    public void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        memberRepository.deleteAll();
    }

    @Test
    @Disabled
    @DisplayName("회원 자동완성 검색")
    public void memberAutoCompleteTest(){

        HashOperations<String, String, Integer> hashOperations = redisTemplate.opsForHash();
        String key = "USERNAME_AUTOCOMPLETE::";
        hashOperations.put(key,"well4149",0);
        hashOperations.put(key,"well123",0);
        hashOperations.put(key,"well",0);

        //키값으로 저장된 값을 가져오기.=>총3개가 들어왔는지 보기.
        Set<String> a = hashOperations.keys(key);
        System.out.println(a);
        //redis scan을 사용해서 well로 시작하는 단어를 전부 다 검색하기.
        ScanOptions scanOptions = ScanOptions.scanOptions().match("well*").build();
        Cursor<Map.Entry<String,Integer>> cursor= hashOperations.scan(key, scanOptions);

        List<String> searchList = new ArrayList<>();

        while(cursor.hasNext()){
            Map.Entry<String,Integer> entry = cursor.next();
            searchList.add(entry.getKey());
        }

        Assertions.assertThat(searchList).isNotNull();
    }
    
    @Test
    @DisplayName("회원 아이디 자동완성 keys 테스트")
    public void testMemberAutoSearchKeys(){
        // Measure performance of the KEYS-based implementation
        long startTime = System.nanoTime();
        List<String> searchList = redisService.memberAutoSearchKeys("user5060");
        long endTime = System.nanoTime();

        System.out.println("KEYS-based search time: " + (endTime - startTime) + " ns");
        System.out.println("Number of keys found: " + searchList.size());
    }

    @Test
    @DisplayName("회원 아이디 자동완성 scan 테스트")
    public void testMemberAutoSearchScan(){
        // Measure performance of the SCAN-based implementation
        long startTime = System.nanoTime();
        List<String> searchList = redisService.memberAutoSearch("user5060");
        long endTime = System.nanoTime();

        System.out.println("SCAN-based search time: " + (endTime - startTime) + " ns");
        System.out.println("Number of keys found: " + searchList.size());
    }
    
}
