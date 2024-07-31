package com.example.coffies_vol_02.testCommnet;

import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.redis.RedissonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CommentRedisTest {

    @Autowired
    private RedissonService redissonService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    public void setUp(){
        // 필요한 설정 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("가게 댓글 좋아요 증가 테스트")
    public void CommentLikeCountUpTest(){

    }

    @Test
    @DisplayName("가게 댓글 좋아요 감소 테스트")
    public void CommentLikeCountDownTest(){

    }

    @Test
    @DisplayName("댓글 평점 삽입 테스트")
    public void placeCommentRateInsertTest(){
        String storeId = "1";
        double rating = 4.5;

        redisService.saveRating(storeId, rating);

        Set<String> ratings = redisTemplate.opsForZSet().range("storeRatings::" + storeId, 0, -1);
        System.out.println(ratings);
        assertThat(ratings.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 평점 삭제 테스트")
    public void placeCommentRateDeleteTest(){
        String storeId = "1";
        double rating = 4.5;
        //평점 삽입
        redisTemplate.opsForZSet().add("storeRatings::" + storeId, storeId, rating);
        //평점 삭제
        redisService.deleteRating(storeId);

        Set<String> ratings = redisTemplate.opsForZSet().range("storeRatings::" + storeId, 0, -1);
        assertThat(ratings.contains(storeId));
    }

}
