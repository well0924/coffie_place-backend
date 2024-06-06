package com.example.coffies_vol_02.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class CacheKey {

    public static final int DEFAULT_EXPIRE_SEC = 60; // 1 minutes

    public static final String USERNAME = "USERNAME_AUTOCOMPLETE::";

    public static final String USER = "USER";

    public static final int USER_EXPIRE_SEC = 60 * 5; // 5 minutes

    public static final String BOARD = "BOARD";

    public static final int BOARD_EXPIRE_SEC = 60 * 10; // 10 minutes

    public static final String LIKES = "LIKES";

    public static final int LIKES_EXPIRED_SEC = 60* 10;

    public static final String NOTICE_BOARD = "NOTICE_BOARD";

    public static final int NOTICE_BOARD_EXPIRE_SEC = 60 *10; // 10 minutes

    public static final String PLACE = "PLACE";

    public static final int PLACE_EXPIRED_SEC = 60*10;

    public static final String COMMENT_RATING_KEY = "COMMENT";
}
