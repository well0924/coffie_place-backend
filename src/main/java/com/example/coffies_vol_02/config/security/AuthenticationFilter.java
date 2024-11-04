package com.example.coffies_vol_02.config.security;

import com.example.coffies_vol_02.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final static String LOGIN_URL = "/api/member/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 로그인 URL인지 확인
        if (request.getRequestURI().equals(LOGIN_URL)) {
            log.info("Login request detected.");
            // 로그인 요청 처리 로직 -> 성공 했을 시와 실패했을 경우
            Member member = (Member) request.getSession().getAttribute("member");

            if(!Objects.isNull(member)) {
                SecurityContextHolder.getContext().setAuthentication(createAuthentication(member));
            }
        }
        // 필터 체인을 계속 실행
        filterChain.doFilter(request, response);
    }

    private Authentication createAuthentication(Member member) {
        // 인증 객체 생성 로직
        return new UsernamePasswordAuthenticationToken(member.getUserId(), member.getPassword(), Collections.singleton(new SimpleGrantedAuthority(member.getRole().toString())));
    }
}
