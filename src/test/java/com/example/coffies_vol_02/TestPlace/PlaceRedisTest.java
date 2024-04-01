package com.example.coffies_vol_02.TestPlace;

import com.example.coffies_vol_02.config.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PlaceRedisTest {

    @Autowired
    RedisOperations<String, String> operations;

    @Autowired
    RedisService redisService;

    private GeoOperations<String,String>geoOperations;

    @BeforeEach
    public void init(){
        geoOperations = operations.opsForGeo();
        geoOperations.add("CafeStore",new Point(13.361389, 38.115556),"place1");
        geoOperations.add("CafeStore",new Point(15.087269, 37.502669),"place2");
        geoOperations.add("CafeStore",new Point(13.583333, 37.316667),"place3");
    }

    @Test
    @DisplayName("geo radius test")
    public void test1(){

        var byDistance = geoOperations.radius("CafeStore", "place3", new Distance(100, RedisGeoCommands.DistanceUnit.KILOMETERS));

        System.out.println(byDistance);

        assertThat(byDistance).hasSize(2).extracting("content.name").contains("place1", "place3");

        var greaterDistance = geoOperations.radius("CafeStore", "place3", new Distance(200, RedisGeoCommands.DistanceUnit.KILOMETERS));

        System.out.println(greaterDistance);

        assertThat(greaterDistance).hasSize(3).extracting("content.name").contains("place1", "place2", "place3");
    }

    @Test
    @DisplayName("가게 자동완성기능")
    public void redisAutoCompleteTest(){
        //redisService.setValues("well4149","무너미");

        List<String>result=redisService.getSearchList("well4149");

        System.out.println(result);
    }
}
