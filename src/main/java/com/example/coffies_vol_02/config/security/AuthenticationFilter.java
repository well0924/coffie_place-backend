package com.example.coffies_vol_02.config.security;


import com.example.coffies_vol_02.config.security.handler.LoginFailHandler;
import com.example.coffies_vol_02.config.security.handler.LoginSuccessHandler;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.RequiredArgsConstructor;
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
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final static String LOGIN_URL = "/api/member/login";

    private final LoginSuccessHandler loginSuccessHandler;

    private final LoginFailHandler loginFailHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Member user = (Member) request.getSession().getAttribute("member");

        if(request.getRequestURI().equals(LOGIN_URL)){
            log.info("login !!");
            try{
                //인증 정보가 있는경우
                if(!Objects.isNull(user)) {
                    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString()); // 사용자 권한
                    log.info(authority);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(authority)); // 현재 사용자의 인증 정보
                    log.info(authentication);
                    log.info("filter member data:::"+user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    //인증 성공시 successHandler 작동
                    loginSuccessHandler.onAuthenticationSuccess(request,response,authentication);
                } else {
                    throw new AuthenticationException("User is null") {};
                }
            } catch (AuthenticationException e) {
                e.getMessage();
                //인증 실패시 loginFailHandler 작동
                loginFailHandler.onAuthenticationFailure(request,response,e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
