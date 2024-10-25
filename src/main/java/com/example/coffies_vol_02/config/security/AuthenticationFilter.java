package com.example.coffies_vol_02.config.security;

import com.example.coffies_vol_02.member.domain.Member;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {

    private final static String LOGIN_URL = "/api/member/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // 세션이 존재하면 가져오고, 없으면 null 반환
        Member member = (session != null) ? (Member) session.getAttribute("member") : null; // Redis 세션에서 가져온 값
        String authorizationHeader = request.getHeader("Authorization");

        log.info(authorizationHeader);
        // 로그인 URL인지 확인
        if(request.getRequestURI().equals(LOGIN_URL)){
            log.info("login !!");
            try{
                //인증 정보가 있는경우
                if(!Objects.isNull(member)) {
                    GrantedAuthority authority = new SimpleGrantedAuthority(member.getRole().toString()); // 사용자 권한
                    log.info(authority);
                    Authentication authentication = createAuthentication(member);
                    log.info(authentication);
                    log.info("filter member data:::"+member);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return;
                }
            } catch (AuthenticationException e) {
                e.getMessage();
                return;
            }
        }
        // 필터 체인을 계속 실행
        filterChain.doFilter(request, response);
    }

    private Authentication createAuthentication(Member member) {
        // 인증 객체 생성 로직
        return new UsernamePasswordAuthenticationToken(member, null, Collections.singleton(new SimpleGrantedAuthority(member.getRole().toString())));
    }
}
