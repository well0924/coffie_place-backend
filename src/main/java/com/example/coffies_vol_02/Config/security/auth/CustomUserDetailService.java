package com.example.coffies_vol_02.Config.security.auth;

import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Config.Redis.CacheKey;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
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

    //자주 사용하는 메소드는 캐시처리하기.
    //redis 캐시에 저장이 되면 user::회원아이디 로 저장이 된다.
    //redis에 저장된 값 출력하기.->게시글 조회시 좋아요기능
    @Cacheable(value = CacheKey.USER,key = "#username",cacheManager = "redisCacheManager")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("----security login in....");
        log.info(username);
        Optional<Member>userdetail = Optional.ofNullable(memberRepository.findByUserId(username).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));
        Member member = userdetail.get();
        log.info(userdetail);
        return new CustomUserDetails(member);
    }
}
