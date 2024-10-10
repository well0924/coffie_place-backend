package com.example.coffies_vol_02.config.crawling.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@RedisHash(value = "PLACE" , timeToLive = 3600)
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCache implements Serializable {
    @Id
    private String placeId; // 가게 ID
    private String placeName;
    private String placeAddr;
    private String placeStart;
    private String placeClose;
    private String placePhone;
    private String placeAuthor;

}
