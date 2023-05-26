package com.example.coffies_vol_02.config.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
@AllArgsConstructor
public class CacheKey {
    public static final int DEFAULT_EXPIRE_SEC = 60; // 1 minutes
    public static final String USER = "user";
    public static final String USERNAME = "USERNAME::";
    public static final int USER_EXPIRE_SEC = 60 * 5; // 5 minutes
    public static final String BOARD = "board";
    public static final int BOARD_EXPIRE_SEC = 60 * 1; // 1 minutes
    public static final String LIKE = "like";
    public static final int LIKE_EXPIRE_SEC = 60 * 10; // 10 minutes
    public static final String NOTICE_BOARD="notice";
    public static final int NOTICE_BOARD_EXPIRE_SEC = 60 * 1; // 1 minutes
}
