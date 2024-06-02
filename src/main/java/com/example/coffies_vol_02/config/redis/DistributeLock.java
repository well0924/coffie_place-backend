package com.example.coffies_vol_02.config.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {
    //락의 이름
    String key();
    //시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    //락을 획득하기 위한 시간
    long waitTime() default 5L;
    //락을 임대하는 시간
    long leaseTime() default 3L;
}
