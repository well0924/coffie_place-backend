package com.example.coffies_vol_02.member.service;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetailService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.request.LoginDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class AuthService {

    private final BCryptPasswordEncoder encoder;

    private final CustomUserDetailService customUserDetailService;

    public String login(LoginDto loginDto, HttpSession httpSession){
        log.info("service");
        // 사용자 정보 조회
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(loginDto.getUserId());

        // 사용자 정보가 존재하지 않거나 비밀번호가 일치하지 않는 경우 예외 발생
        if(!encoder.matches(loginDto.getPassword(), customUserDetails.getMember().getPassword())) {
            throw new CustomExceptionHandler(ERRORCODE.PASSWORD_NOT_MATCH);
        }
        // 세션에 사용자 정보 저장
        Member member = customUserDetails.getMember();
        httpSession.setAttribute("member", member);

        log.info("세션에 저장된 사용자 정보: {}", member);

        return httpSession.getId();
    }

    public void logout(HttpSession httpSession){
        httpSession.removeAttribute("member");
    }
}
