package com.example.coffies_vol_02.testCommnet;

import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CommentRepository commentRepository;


    @Test
    @DisplayName("가게 댓글작성 평점 저장")
    public void placeCommentAverageTest(){

    }

    @Test
    @DisplayName("가게 댓글 삭제 평점 수정")
    public void placeCommentAverageDeleteTest(){

    }

    @Test
    @DisplayName("가게 평점 댓글 top5 가게 목록")
    public void placeCommentAverageSortedSetTop5Test(){

    }
}
