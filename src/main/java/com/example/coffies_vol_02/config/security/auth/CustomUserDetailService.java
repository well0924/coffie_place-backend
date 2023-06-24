package com.example.coffies_vol_02.config.security.auth;

import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /** 
     * 시큐리티 로그인
     * redis 캐시에 저장이 되면 user::회원아이디 로 저장이 된다.
     * @author 양경빈
     * @param username 회원 아이디
     * @throws UsernameNotFoundException 회원아이디가 없을 경우 Exception 을 발생
     * @return UserDetail 로그인시 확인된 회원 객체
     **/
    @Cacheable(value = CacheKey.USER,key = "#username",unless = "#result == null",cacheManager = "redisCacheManager")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("----security login in....");
        log.info(username);

        Optional<Member>userDetail = Optional
                .ofNullable(memberRepository.findByUserId(username)
                        .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Member member = userDetail
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER));

        log.info(userDetail);

        return new CustomUserDetails(member);
    }
}
